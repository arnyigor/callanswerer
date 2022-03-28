package com.arny.callanswerer.presentation

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arny.callanswerer.data.player.Player
import com.arny.callanswerer.data.voice.VoiceDemoAnalyzer
import com.arny.callanswerer.data.voice.VoiceRecognizer
import com.arny.callanswerer.databinding.FragmentMainBinding
import com.arny.callanswerer.di.appComponent
import com.arny.callanswerer.presentation.extentions.requestPermission
import com.arny.callanswerer.presentation.extentions.safeWith
import javax.inject.Inject
import kotlin.properties.Delegates

class MainFragment : Fragment() {
    private var filesListAdapter: FilesListAdapter? = null
    private var binding: FragmentMainBinding? = null
    private val requestPhoneState = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        requestReadPhoneLog()
    }
    private val requestLog = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        requestAudioPermission()
    }
    private val requestAudio = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        initPlayer()
    }

    @Inject
    lateinit var player: Player

    @Inject
    lateinit var voiceRecognizer: VoiceRecognizer

    @Inject
    lateinit var analyzer: VoiceDemoAnalyzer

    private var btnState by Delegates.observable(VoiceRecognizer.RecognizeState.STATE_DESTROED) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            binding?.btnPlay?.text = newValue.toString()
            when (newValue) {
                VoiceRecognizer.RecognizeState.STATE_READY,
                VoiceRecognizer.RecognizeState.STATE_DONE,
                VoiceRecognizer.RecognizeState.STATE_MIC,
                -> binding?.btnPlay?.isEnabled = true
                else -> binding?.btnPlay?.isEnabled = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = FragmentMainBinding.inflate(inflater, container, false)
        binding = inflate
        return inflate.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        voiceRecognizer.destroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.safeWith {
            filesListAdapter = FilesListAdapter {
                playFile(it)
            }
            rvFiles.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            rvFiles.adapter = filesListAdapter
            btnPlay.setOnClickListener {
                if (voiceRecognizer.state == VoiceRecognizer.RecognizeState.STATE_MIC) {
                    voiceRecognizer.stopRecognize()
                } else {
                    voiceRecognizer.recognize()
                }
            }
        }
        requestReadPhoneState {
            requestReadPhoneLog {
                requestAudioPermission {
                    initPlayer()
                }
            }
        }
    }

    private fun playFile(it: String) {
        voiceRecognizer.pause(true)
        player.playFile(name = it) {
            voiceRecognizer.pause(false)
        }
    }

    private fun requestReadPhoneState(onResult: (input: String) -> Unit = {}) {
        requestPermission(
            requestPhoneState,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_STATE, onResult
        )
    }

    private fun requestReadPhoneLog(onResult: (input: String) -> Unit = {}) {
        requestPermission(
            requestLog,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CALL_LOG, onResult
        )
    }

    private fun requestAudioPermission(onResult: (input: String) -> Unit = {}) {
        requestPermission(
            requestAudio,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.RECORD_AUDIO, onResult
        )
    }

    private fun initPlayer() {
        val allFiles = player.getAllFiles()
        filesListAdapter?.submitList(allFiles)
        with(voiceRecognizer) {
            setErrorChange {
                binding?.tvInfo?.text = "ERROR:$it"
            }
            setStateChange {
                btnState = it
            }
            setTextResult { text ->
                binding?.tvInfo?.text = text
                val analyze = analyzer.analyze(text)
                if (analyze.isNotBlank()) {
                    playFile(analyze)
                }
            }
            init()
        }
    }
}