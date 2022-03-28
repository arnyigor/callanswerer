package com.arny.callanswerer.data.voice

import android.content.Context
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService
import java.io.IOException
import javax.inject.Inject

class VoiceRecognizer @Inject constructor(
    private val context: Context,
) : RecognitionListener {
    enum class RecognizeState {
        STATE_START,
        STATE_READY,
        STATE_DONE,
        STATE_MIC,
        STATE_DESTROED
    }
    private var onTextResult: (message: String) -> Unit = {}
    private var onStateChange: (state: RecognizeState) -> Unit = {}
    private var onError: (message: String?) -> Unit = {}
    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var speechStreamService: SpeechStreamService? = null

    fun setTextResult(result: (message: String) -> Unit){
        this.onTextResult = result
    }

    fun setStateChange(state: (state: RecognizeState) -> Unit){
        this.onStateChange = state
    }

    fun setErrorChange(error: (message: String?) -> Unit){
        this.onError = error
    }

    fun init() {
        onStateChange(RecognizeState.STATE_START)
        StorageService.unpack(
            context,
            "model-ru-ru",
            "model",
            { model: Model? ->
                this.model = model
                onStateChange(RecognizeState.STATE_READY)
            }, { exception: IOException ->
                onError("Failed to unpack the model" + exception.message)
            }
        )
    }

    fun stopRecognize() {
        if (speechService != null) {
            onStateChange(RecognizeState.STATE_DONE)
            speechService?.stop()
            speechService = null
        }
    }

    fun recognize() {
        onStateChange(RecognizeState.STATE_MIC)
        try {
            val rec = Recognizer(model, 16000.0f)
            speechService = SpeechService(rec, 16000.0f)
            speechService?.startListening(this)
        } catch (e: IOException) {
            onError(e.message)
        }
    }

    fun pause(paused: Boolean) {
        speechService?.setPause(paused)
    }

    fun destroy() {
        if (speechService != null) {
            speechService?.stop()
            speechService?.shutdown()
            speechService = null
        }
        speechStreamService?.stop()
        onStateChange(RecognizeState.STATE_DESTROED)
    }

    override fun onPartialResult(hypothesis: String?) {}

    override fun onResult(hypothesis: String?) {
        if (!hypothesis.isNullOrBlank()) {
            onTextResult(hypothesis)
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        onStateChange(RecognizeState.STATE_DONE)
        if (speechStreamService != null) {
            speechStreamService = null;
        }
    }

    override fun onError(exception: Exception?) {
        exception?.printStackTrace()
    }

    override fun onTimeout() {
        onStateChange(RecognizeState.STATE_DONE)
    }
}
