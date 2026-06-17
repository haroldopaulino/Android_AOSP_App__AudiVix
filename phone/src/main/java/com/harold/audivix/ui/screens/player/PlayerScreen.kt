package com.harold.audivix.ui.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.harold.audivix.player.PlayerController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(playerController: PlayerController) {
    val state by playerController.state.collectAsState()
    val media = state.current
    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Now Playing") })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (media == null) {
                Text("Nothing is playing")
                return@Column
            }
            AsyncImage(
                model = media.artworkUrl,
                contentDescription = media.title,
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(36.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(28.dp))
            Text(media.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(media.artist.ifBlank { "Unknown artist" }, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Slider(
                value = state.positionMs.toFloat(),
                onValueChange = { playerController.seekTo(it.toLong()) },
                valueRange = 0f..state.durationMs.coerceAtLeast(1).toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            Text("${formatTime(state.positionMs)} / ${formatTime(state.durationMs)}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(24.dp))
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(onClick = playerController::toggle, modifier = Modifier.size(72.dp)) {
                    Icon(if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Play or pause")
                }
                IconButton(onClick = playerController::stop) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("Background playback uses Android media controls. The notification shade exposes play, pause, stop, seek, and progress controls through Media3 MediaSession.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
