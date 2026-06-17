package com.harold.audivix.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val serviceIntent = Intent(context, AudiVixPlaybackService::class.java).apply {
            this.action = action
        }
        context.startForegroundService(serviceIntent)
    }
}
