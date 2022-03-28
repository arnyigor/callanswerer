package com.arny.callanswerer.data.player

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import java.io.IOException
import javax.inject.Inject
import kotlin.math.ln

class Player @Inject constructor(
    private val context: Context
) {
    private var locked = false

    private companion object {
        const val MAX_VOLUME = 100.0
    }

    fun getAllFiles(): List<String> = context.resources.assets.list("mp3")?.toList() ?: emptyList()

    private var mediaPlayer: MediaPlayer? = null
    fun playFile(name: String, onComplete: () -> Unit = {}) {
        if (!locked) {
            if (mediaPlayer == null) {
                // The volume on STREAM_SYSTEM is not adjustable, and users found it
                // too loud,
                // so we now play on the music stream.
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer?.setOnCompletionListener {
                    onComplete()
                    onRelease()
                }
                val file: AssetFileDescriptor = context.resources.assets.openFd("mp3/$name")
                try {
                    mediaPlayer?.setDataSource(file.fileDescriptor, file.startOffset, file.length)
                    file.close()
                    val volume = (1 - (ln(MAX_VOLUME - 100) / ln(MAX_VOLUME))).toFloat()
                    mediaPlayer?.setVolume(volume, volume)
                    mediaPlayer?.prepare()
                    mediaPlayer?.start()
                    locked = true
                } catch (e: IOException) {
                    mediaPlayer = null
                    locked = false
                }
            }
        }
    }

    private fun onRelease() {
        mediaPlayer = null
        locked = false
    }
}