package com.harold.audivix.wear.ui.screens.home

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.Text
import coil3.compose.AsyncImage
import com.harold.audivix.wear.data.model.WearMediaItem
import com.harold.audivix.wear.data.model.WearMediaType
import com.harold.audivix.wear.R
import com.harold.audivix.wear.ui.theme.wearAccentColor
import com.harold.audivix.wear.ui.theme.wearBackground
import com.harold.audivix.wear.ui.theme.wearButtonColor
import com.harold.audivix.wear.ui.theme.wearButtonTextColor
import com.harold.audivix.wear.ui.theme.wearCardBorderColor
import com.harold.audivix.wear.ui.theme.wearCardColor
import com.harold.audivix.wear.ui.theme.wearSecondaryTextColor
import com.harold.audivix.wear.ui.theme.wearTextColor
import com.harold.audivix.wear.viewmodel.WearHomeUiState

@Composable
fun WearHomeScreen(
    uiState: WearHomeUiState,
    darkTheme: Boolean,
    player: ExoPlayer,
    onMediaClick: (WearMediaItem) -> Unit,
    onMove: (Int, Int) -> Unit,
    onSettings: () -> Unit,
    onRefresh: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(wearBackground(darkTheme))
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wearable),
                        contentDescription = "AudiVix banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .padding(horizontal = 6.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        ActionButton("Settings", darkTheme, onClick = onSettings)
                    }
                }
            }
            if (uiState.isLoading) {
                item { CircularProgressIndicator(modifier = Modifier.size(28.dp)) }
            }
            uiState.error?.let { message ->
                item {
                    Text(
                        text = message,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 2,
                        color = wearTextColor(darkTheme)
                    )
                }
            }
            itemsIndexed(uiState.items) { index: Int, item: WearMediaItem ->
                WearMediaRow(
                    item = item,
                    darkTheme = darkTheme,
                    isPlaying = uiState.selectedId == item.id && uiState.isPlaying,
                    showMiniPlayer = uiState.showMiniPlayerForVideos &&
                        uiState.selectedId == item.id &&
                        item.type == WearMediaType.Video,
                    player = player,
                    index = index,
                    count = uiState.items.size,
                    onClick = { onMediaClick(item) },
                    onMove = onMove
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .background(wearButtonColor(darkTheme), RoundedCornerShape(24.dp))
    ) {
        Text(
            text = text,
            color = wearButtonTextColor(darkTheme),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun WearMediaRow(
    item: WearMediaItem,
    darkTheme: Boolean,
    isPlaying: Boolean,
    showMiniPlayer: Boolean,
    player: ExoPlayer,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    onMove: (Int, Int) -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(wearCardColor(darkTheme))
            .border(
                width = if (darkTheme) 0.dp else 1.dp,
                color = wearCardBorderColor(darkTheme),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Box(modifier = Modifier.background(wearCardColor(darkTheme))) {
            if (isPlaying && item.artworkUrl.isNotBlank()) {
                AsyncImage(
                    model = item.artworkUrl,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.horizontalGradient(
                                if (darkTheme) {
                                    listOf(Color.Black.copy(alpha = 0.78f), Color.Black.copy(alpha = 0.48f))
                                } else {
                                    listOf(Color.White.copy(alpha = 0.86f), Color.White.copy(alpha = 0.62f))
                                }
                            )
                        )
                )
            }
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MediaTypeIcon(type = item.type, darkTheme = darkTheme)
                    AsyncImage(
                        model = item.thumbnailUrl,
                        contentDescription = item.title,
                        modifier = Modifier
                            .padding(start = 6.dp, end = 8.dp)
                            .size(38.dp)
                            .clip(RoundedCornerShape(9.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        Text(
                            text = item.title,
                            maxLines = 1,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isPlaying && darkTheme) Color.White else wearTextColor(darkTheme),
                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                        )
                        Text(
                            text = item.artist,
                            maxLines = 1,
                            color = if (isPlaying && darkTheme) Color.White.copy(alpha = 0.88f) else wearSecondaryTextColor(darkTheme),
                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                        )
                        if (isPlaying) {
                            EqualizerBars()
                        }
                    }
                    DragHandle(index = index, count = count, onMove = onMove)
                }
                if (showMiniPlayer) {
                    WearMiniVideoPlayer(
                        player = player,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WearMiniVideoPlayer(
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    var fullscreen by remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { fullscreen = true },
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                this.player = player
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { view ->
            view.player = player
        }
    )

    if (fullscreen) {
        Dialog(
            onDismissRequest = { fullscreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { fullscreen = false },
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        PlayerView(context).apply {
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            this.player = player
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { view ->
                        view.player = player
                    }
                )
                Button(
                    onClick = { fullscreen = false },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 7.dp)
                        .height(29.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun EqualizerBars() {
    val transition = rememberInfiniteTransition(label = "wear-equalizer")
    val a by transition.animateFloat(0.25f, 1f, infiniteRepeatable(tween(260), RepeatMode.Reverse), label = "a")
    val b by transition.animateFloat(1f, 0.35f, infiniteRepeatable(tween(340), RepeatMode.Reverse), label = "b")
    val c by transition.animateFloat(0.45f, 1f, infiniteRepeatable(tween(420), RepeatMode.Reverse), label = "c")
    Canvas(modifier = Modifier.size(width = 34.dp, height = 14.dp)) {
        listOf(a, b, c, b, a).forEachIndexed { index, height ->
            val barWidth = size.width / 9f
            val x = index * barWidth * 1.65f
            val h = size.height * height
            drawRoundRect(
                color = Color(0xFF20D86B),
                topLeft = Offset(x, size.height - h),
                size = Size(barWidth, h),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}

@Composable
private fun MediaTypeIcon(type: WearMediaType, darkTheme: Boolean) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val iconColor = wearAccentColor(darkTheme)
        if (type == WearMediaType.Video) {
            drawRoundRect(
                color = iconColor,
                topLeft = Offset(size.width * 0.14f, size.height * 0.25f),
                size = Size(size.width * 0.5f, size.height * 0.5f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            val path = Path().apply {
                moveTo(size.width * 0.64f, size.height * 0.38f)
                lineTo(size.width * 0.88f, size.height * 0.28f)
                lineTo(size.width * 0.88f, size.height * 0.72f)
                lineTo(size.width * 0.64f, size.height * 0.62f)
                close()
            }
            drawPath(path, iconColor)
        } else {
            drawRoundRect(
                color = iconColor,
                topLeft = Offset(size.width * 0.16f, size.height * 0.38f),
                size = Size(size.width * 0.22f, size.height * 0.26f),
                cornerRadius = CornerRadius(3f, 3f)
            )
            val cone = Path().apply {
                moveTo(size.width * 0.38f, size.height * 0.38f)
                lineTo(size.width * 0.62f, size.height * 0.24f)
                lineTo(size.width * 0.62f, size.height * 0.78f)
                lineTo(size.width * 0.38f, size.height * 0.64f)
                close()
            }
            drawPath(cone, iconColor)
            drawArc(
                color = iconColor,
                startAngle = -35f,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(size.width * 0.54f, size.height * 0.30f),
                size = Size(size.width * 0.32f, size.height * 0.42f),
                style = Stroke(width = 2.2f)
            )
        }
    }
}

@Composable
private fun DragHandle(index: Int, count: Int, onMove: (Int, Int) -> Unit) {
    var dragAmount by remember { mutableFloatStateOf(0f) }
    Canvas(
        modifier = Modifier
            .size(24.dp)
            .pointerInput(index, count) {
                detectDragGestures(
                    onDragEnd = { dragAmount = 0f },
                    onDragCancel = { dragAmount = 0f }
                ) { change, drag ->
                    change.consume()
                    dragAmount += drag.y
                    if (dragAmount > 18f && index < count - 1) {
                        onMove(index, index + 1)
                        dragAmount = 0f
                    }
                    if (dragAmount < -18f && index > 0) {
                        onMove(index, index - 1)
                        dragAmount = 0f
                    }
                }
            }
    ) {
        val dot = size.minDimension * 0.1f
        listOf(size.width * 0.38f, size.width * 0.62f).forEach { x ->
            listOf(size.height * 0.3f, size.height * 0.5f, size.height * 0.7f).forEach { y ->
                drawCircle(Color.White.copy(alpha = 0.75f), dot, Offset(x, y))
            }
        }
    }
}
