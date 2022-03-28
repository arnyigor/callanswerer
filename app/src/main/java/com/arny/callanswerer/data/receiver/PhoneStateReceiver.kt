package com.arny.callanswerer.data.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.arny.callanswerer.presentation.MainActivity
import com.arny.callanswerer.presentation.extentions.dump

class PhoneStateReceiver : BroadcastReceiver() {
    private companion object {
        const val TAG = "PhoneStatReceiver"
        var incomingFlag = false
        var incoming_number: String? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(PhoneStateReceiver::class.java.simpleName, "onReceive: intent:${intent.dump()}")
        if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            incomingFlag = false
            val phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            Log.i(TAG, "call OUT:$phoneNumber")
        } else {
            val tm = context.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (tm.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    incomingFlag = true
                    incoming_number = intent.getStringExtra("incoming_number")
                    Log.i(TAG, "RINGING :$incoming_number")
                    val intent1 = Intent(context, MainActivity::class.java)
                    intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent1)
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> if (incomingFlag) {
                    Log.i(TAG, "incoming ACCEPT :$incoming_number")
                }
                TelephonyManager.CALL_STATE_IDLE -> if (incomingFlag) {
                    Log.i(TAG, "incoming IDLE")
                }
            }
        }
    }

    private fun showNotification(context: Context, number: String) {
        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "channel_name"
                val descriptionText = "channel_description"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                    description = descriptionText
                }
                this.createNotificationChannel(channel)
            }
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val builder = NotificationCompat.Builder(context, "111")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Call")
                .setContentText(number)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
            notify(777, builder.build())
        }
    }
}