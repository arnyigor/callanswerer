package com.arny.callanswerer.data.voice

import android.content.Context
import android.util.Log
import org.json.JSONObject
import javax.inject.Inject

class VoiceDemoAnalyzer @Inject constructor(
    context: Context
) {
    private companion object {
        const val TEXT_KEY = "text"
    }

    private val files: List<String> = context.resources.assets.list("mp3")?.toList() ?: emptyList()

    fun analyze(message: String): String {
        val jsonObject = JSONObject(message)
        Log.i(VoiceDemoAnalyzer::class.java.simpleName, "analyze: jsonObject:$jsonObject")
        if (jsonObject.has(TEXT_KEY)) {
            return getMessage(jsonObject.get(TEXT_KEY).toString())
        }
        return ""
    }

    private fun getMessage(message: String): String {
        return if (message.lowercase().contains("привет")) {
            files.toMutableList().shuffled().first()
        } else {
            ""
        }
    }
}