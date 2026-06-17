package com.harold.audivix.player

import com.harold.audivix.data.model.AudiVixMedia

data class PlayerState(
    val current: AudiVixMedia? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val bufferedMs: Long = 0
)
