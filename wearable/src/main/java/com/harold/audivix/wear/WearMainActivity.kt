package com.harold.audivix.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harold.audivix.wear.player.WearPlayerState
import com.harold.audivix.wear.ui.navigation.WearRoutes
import com.harold.audivix.wear.ui.screens.home.WearHomeScreen
import com.harold.audivix.wear.ui.screens.settings.WearSettingsScreen
import com.harold.audivix.wear.ui.theme.AudiVixWearTheme
import com.harold.audivix.wear.viewmodel.WearHomeViewModel
import com.harold.audivix.wear.viewmodel.WearSettingsViewModel
import com.harold.audivix.wear.viewmodel.WearViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow

class WearMainActivity : ComponentActivity() {
    private lateinit var player: ExoPlayer
    private val playerState = MutableStateFlow(WearPlayerState())
    private var userStoppedPlayback = false
    private var currentMediaList: List<com.harold.audivix.wear.data.model.WearMediaItem> = emptyList()
    private var autoPlayNext = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = ExoPlayer.Builder(this).build()
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED && autoPlayNext && !userStoppedPlayback) {
                    playNextWearMedia()
                } else if (playbackState == Player.STATE_ENDED) {
                    playerState.value = WearPlayerState(playerState.value.mediaId, false)
                }
            }
        })
        val repository = (application as WearAudiVixApp).repository
        val factory = WearViewModelFactory(repository)

        setContent {
            DisposableEffect(Unit) {
                onDispose {
                    player.release()
                }
            }

            val settingsViewModel: WearSettingsViewModel = viewModel(factory = factory)
            val darkTheme by settingsViewModel.darkTheme.collectAsState()

            AudiVixWearTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = WearRoutes.Home) {
                    composable(WearRoutes.Home) {
                        val viewModel: WearHomeViewModel = viewModel(factory = factory)
                        val state by viewModel.uiState(playerState).collectAsState()
                        currentMediaList = state.items
                        autoPlayNext = state.autoPlayNextMedia
                        WearHomeScreen(
                            uiState = state,
                            darkTheme = darkTheme,
                            player = player,
                            onMediaClick = { media ->
                                if (playerState.value.mediaId == media.id) {
                                    if (player.isPlaying) {
                                        userStoppedPlayback = true
                                        player.pause()
                                        playerState.value = WearPlayerState(media.id, false)
                                    } else {
                                        userStoppedPlayback = false
                                        player.play()
                                        playerState.value = WearPlayerState(media.id, true)
                                    }
                                } else {
                                    userStoppedPlayback = false
                                    player.setMediaItem(MediaItem.fromUri(media.streamUrl))
                                    player.prepare()
                                    player.play()
                                    playerState.value = WearPlayerState(media.id, true)
                                }
                            },
                            onMove = viewModel::moveItem,
                            onSettings = { navController.navigate(WearRoutes.Settings) },
                            onRefresh = viewModel::refresh
                        )
                    }
                    composable(WearRoutes.Settings) {
                        WearSettingsScreen(
                            viewModel = settingsViewModel,
                            darkTheme = darkTheme,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    private fun playNextWearMedia() {
        if (currentMediaList.isEmpty()) {
            playerState.value = WearPlayerState(playerState.value.mediaId, false)
            return
        }
        val currentId = playerState.value.mediaId
        val currentIndex = currentMediaList.indexOfFirst { it.id == currentId }
        val nextIndex = if (currentIndex == -1 || currentIndex == currentMediaList.lastIndex) 0 else currentIndex + 1
        val next = currentMediaList[nextIndex]
        player.setMediaItem(MediaItem.fromUri(next.streamUrl))
        player.prepare()
        player.play()
        playerState.value = WearPlayerState(next.id, true)
    }
}
