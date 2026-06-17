package com.harold.audivix.ui.screens.home

import android.view.ViewGroup
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.max
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.harold.audivix.data.model.AudiVixMedia
import kotlin.math.roundToLong

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReorderableMediaCard(
    modifier: Modifier = Modifier,
    media: AudiVixMedia,
    player: Player,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isPreparing: Boolean,
    positionMs: Long,
    durationMs: Long,
    index: Int,
    total: Int,
    onMove: (String, Int) -> Unit,
    onPlay: () -> Unit,
    onSeek: (Long) -> Unit
) {
    val drag = remember { mutableFloatStateOf(0f) }
    val height = when {
        isCurrent && media.isVideo -> 462.dp
        isCurrent -> 190.dp
        else -> 96.dp
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clickable(onClick = onPlay),
        colors = CardDefaults.cardColors(containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            if (isCurrent) {
                AsyncImage(
                    model = media.artworkModel,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.54f)
                                )
                            )
                        )
                )
            }
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AsyncImage(
                        model = media.thumbnailModel,
                        contentDescription = media.title,
                        modifier = Modifier
                            .size(if (isCurrent) 88.dp else 68.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        Text(
                            text = media.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                        )
                        Text(
                            text = media.artist.ifBlank { "Unknown artist" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            media.detailLine(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                        )
                    }
                    if (isCurrent && isPlaying) {
                        PlayingEqualizer(Modifier.size(width = 28.dp, height = 30.dp))
                    }
                    when {
                        isPreparing -> CircularProgressIndicator(Modifier.size(30.dp), strokeWidth = 3.dp)
                        else -> IconButton(onClick = onPlay) {
                            Icon(
                                imageVector = if (isCurrent && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isCurrent && isPlaying) "Pause" else "Play",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    ReorderHandle(
                        modifier = Modifier
                            .size(width = 32.dp, height = 58.dp)
                            .pointerReorder(media.id, total, drag.floatValue, { drag.floatValue = it }, onMove)
                    )
                }
                if (isCurrent) {
                    Spacer(Modifier.height(10.dp))
                    PlaybackProgress(
                        positionMs = positionMs,
                        durationMs = durationMs.takeIf { it > 0 } ?: media.durationMs,
                        onSeek = onSeek
                    )
                    if (media.isVideo) {
                        Spacer(Modifier.height(10.dp))
                        EmbeddedVideoPlayer(player = player)
                    }
                }
            }
        }
    }
}

private fun AudiVixMedia.detailLine(): String {
    val details = listOf(
        genre.takeUnless { it.equals("General", ignoreCase = true) || it.isBlank() },
        compressedFormat.takeIf { it.isNotBlank() },
        "★ ${"%.1f".format(rating)}",
        "$playCount plays"
    ).filterNotNull()
    return details.joinToString(" • ")
}

@Composable
private fun EmbeddedVideoPlayer(player: Player) {
    var fullscreen by remember { mutableStateOf(false) }
    if (!fullscreen) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .height(200.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.Black)
        ) {
            MediaPlayerView(
                player = player,
                modifier = Modifier.fillMaxSize(),
                useController = false,
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT,
                onClick = { fullscreen = true }
            )
        }
    }
    if (fullscreen) {
        Dialog(
            onDismissRequest = { fullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            FullscreenVideoContent(
                player = player,
                onDismiss = { fullscreen = false }
            )
        }
    }
}

@Composable
private fun FullscreenVideoContent(player: Player, onDismiss: () -> Unit) {
    var videoSize by remember { mutableStateOf(player.videoSize) }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(size: VideoSize) {
                videoSize = size
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val screenIsWide = maxWidth > maxHeight
        val videoIsWide = videoSize.width >= videoSize.height
        val hasVideoSize = videoSize.width > 0 && videoSize.height > 0
        val resizeMode = if (!hasVideoSize || screenIsWide == videoIsWide) {
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        } else {
            AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
        MediaPlayerView(
            player = player,
            modifier = Modifier.fillMaxSize(),
            useController = false,
            resizeMode = resizeMode,
            onClick = onDismiss
        )
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp),
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close fullscreen video")
            }
        }
    }
}

@Composable
private fun MediaPlayerView(
    player: Player,
    modifier: Modifier,
    useController: Boolean,
    resizeMode: Int,
    onClick: () -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PlayerView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                this.player = player
                this.useController = useController
                this.resizeMode = resizeMode
                setShutterBackgroundColor(android.graphics.Color.BLACK)
                setOnClickListener { onClick() }
            }
        },
        update = { view ->
            view.player = player
            view.useController = useController
            view.resizeMode = resizeMode
            view.visibility = android.view.View.VISIBLE
            view.setOnClickListener { onClick() }
        }
    )
}

@Composable
private fun PlaybackProgress(positionMs: Long, durationMs: Long, onSeek: (Long) -> Unit) {
    val safeDuration = durationMs.coerceAtLeast(1L)
    var dragging by remember { mutableStateOf(false) }
    var dragValue by remember { mutableFloatStateOf(0f) }
    val progress = if (dragging) dragValue else positionMs.coerceIn(0L, safeDuration).toFloat()
    Slider(
        value = progress,
        onValueChange = {
            dragging = true
            dragValue = it
        },
        onValueChangeFinished = {
            dragging = false
            onSeek(dragValue.roundToLong())
        },
        valueRange = 0f..safeDuration.toFloat(),
        modifier = Modifier.fillMaxWidth()
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Text("${formatTime(progress.roundToLong())} - ${formatTime(safeDuration)}", style = MaterialTheme.typography.labelMedium)
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds) else "%02d:%02d".format(minutes, seconds)
}

@Composable
private fun PlayingEqualizer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "playing_equalizer")
    val h1 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(420), repeatMode = RepeatMode.Reverse),
        label = "eq_bar_1"
    )
    val h2 by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(animation = tween(360), repeatMode = RepeatMode.Reverse),
        label = "eq_bar_2"
    )
    val h3 by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(animation = tween(520), repeatMode = RepeatMode.Reverse),
        label = "eq_bar_3"
    )
    val h4 by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.38f,
        animationSpec = infiniteRepeatable(animation = tween(460), repeatMode = RepeatMode.Reverse),
        label = "eq_bar_4"
    )
    val color = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier) {
        val values = listOf(h1, h2, h3, h4)
        val gap = 3.dp.toPx()
        val barWidth = max(2.dp.toPx(), (size.width - gap * (values.size - 1)) / values.size)
        values.forEachIndexed { index, value ->
            val barHeight = size.height * value.coerceIn(0.18f, 1f)
            val left = index * (barWidth + gap)
            drawRoundRect(
                color = color,
                topLeft = Offset(left, size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}

@Composable
private fun ReorderHandle(modifier: Modifier = Modifier) {
    val dotColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f)
    Canvas(modifier = modifier) {
        val radius = 3.dp.toPx()
        val leftX = size.width * 0.38f
        val rightX = size.width * 0.62f
        val topY = size.height * 0.28f
        val centerY = size.height * 0.50f
        val bottomY = size.height * 0.72f
        listOf(topY, centerY, bottomY).forEach { y ->
            drawCircle(dotColor, radius, Offset(leftX, y))
            drawCircle(dotColor, radius, Offset(rightX, y))
        }
    }
}

private fun Modifier.pointerReorder(itemId: String, total: Int, currentDrag: Float, setDrag: (Float) -> Unit, onMove: (String, Int) -> Unit): Modifier = pointerInput(itemId, total) {
    currentDrag
    var accumulatedDrag = 0f
    val switchThreshold = 54f
    detectDragGestures(
        onDragStart = {
            accumulatedDrag = 0f
            setDrag(0f)
        },
        onDragEnd = {
            accumulatedDrag = 0f
            setDrag(0f)
        },
        onDragCancel = {
            accumulatedDrag = 0f
            setDrag(0f)
        },
        onDrag = { change, dragAmount ->
            change.consume()
            accumulatedDrag += dragAmount.y
            setDrag(accumulatedDrag)
            while (accumulatedDrag >= switchThreshold) {
                onMove(itemId, 1)
                accumulatedDrag -= switchThreshold
                setDrag(accumulatedDrag)
            }
            while (accumulatedDrag <= -switchThreshold) {
                onMove(itemId, -1)
                accumulatedDrag += switchThreshold
                setDrag(accumulatedDrag)
            }
        }
    )
}
