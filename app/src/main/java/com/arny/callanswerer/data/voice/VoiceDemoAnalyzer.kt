package com.arny.callanswerer.data.voice

import android.util.Log

class VoiceDemoAnalyzer {
    fun analyze(message: String): String {
        Log.i(VoiceDemoAnalyzer::class.java.simpleName, "analyze: message:$message");
        if (message.lowercase().contains("привет")) {
            return "pomolchite.mp3"
        }
        return ""
    }
}