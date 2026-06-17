package com.harold.audivix.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.harold.audivix.R
import com.harold.audivix.data.model.MediaType
import com.harold.audivix.player.PlayerController
import com.harold.audivix.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, playerController: PlayerController, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()
    val playerState by playerController.state.collectAsState()
    Column(modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.phone),
            contentDescription = "AudiVix banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(22.dp)),
            contentScale = ContentScale.Crop
        )
        Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Your music and videos, ready whenever you press play.", style = MaterialTheme.typography.bodyLarge)
            PrimaryTabRow(selectedTabIndex = if (state.selectedType == MediaType.Audio) 0 else 1) {
                Tab(selected = state.selectedType == MediaType.Audio, onClick = { viewModel.selectType(MediaType.Audio) }, text = { Text("Audio") })
                Tab(selected = state.selectedType == MediaType.Video, onClick = { viewModel.selectType(MediaType.Video) }, text = { Text("Video") })
            }
        }
        when {
            state.loading -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { CircularProgressIndicator() }
            state.error != null -> Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(state.error ?: "Error")
                AssistChip(onClick = viewModel::refresh, label = { Text("Retry") })
            }
            else -> LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(state.visibleCatalog, key = { _, item -> item.id }) { index, media ->
                    ReorderableMediaCard(
                        modifier = Modifier.animateContentSize(),
                        media = media,
                        player = playerController.player(),
                        isCurrent = playerState.current?.id == media.id,
                        isPlaying = playerState.current?.id == media.id && playerState.isPlaying,
                        isPreparing = state.preparingId == media.id,
                        positionMs = if (playerState.current?.id == media.id) playerState.positionMs else 0L,
                        durationMs = if (playerState.current?.id == media.id) playerState.durationMs else media.durationMs,
                        index = index,
                        total = state.visibleCatalog.size,
                        onMove = viewModel::moveDraggedItem,
                        onPlay = { viewModel.play(media, playerController) },
                        onSeek = playerController::seekTo
                    )
                }
            }
        }
    }
}
