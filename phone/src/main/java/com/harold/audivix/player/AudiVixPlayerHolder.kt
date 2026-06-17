package com.harold.audivix.player

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer

object AudiVixPlayerHolder {
    @Volatile private var instance: ExoPlayer? = null

    fun player(context: Context): ExoPlayer {
        return instance ?: synchronized(this) {
            instance ?: ExoPlayer.Builder(context.applicationContext).build().also { instance = it }
        }
    }

    fun release() {
        instance?.release()
        instance = null
    }
}
