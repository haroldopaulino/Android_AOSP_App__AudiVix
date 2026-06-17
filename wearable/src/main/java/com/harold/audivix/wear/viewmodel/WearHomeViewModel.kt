package com.harold.audivix.wear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harold.audivix.wear.data.model.WearMediaItem
import com.harold.audivix.wear.data.repository.WearMediaRepository
import com.harold.audivix.wear.player.WearPlayerState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WearHomeUiState(
    val items: List<WearMediaItem> = emptyList(),
    val selectedId: String? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showMiniPlayerForVideos: Boolean = true,
    val autoPlayNextMedia: Boolean = true
)

class WearHomeViewModel(private val repository: WearMediaRepository) : ViewModel() {
    fun uiState(playerState: StateFlow<WearPlayerState>): StateFlow<WearHomeUiState> {
        return combine(
            repository.media(),
            repository.loading(),
            repository.error(),
            repository.showMiniPlayerForVideos()
        ) { media, loading, error, showMiniPlayerForVideos ->
            PartialWearHomeState(
                items = media,
                isLoading = loading,
                error = error,
                showMiniPlayerForVideos = showMiniPlayerForVideos
            )
        }.combine(repository.autoPlayNextMedia()) { partial, autoPlayNextMedia ->
            partial.copy(autoPlayNextMedia = autoPlayNextMedia)
        }.combine(playerState) { partial, player ->
            WearHomeUiState(
                items = partial.items,
                selectedId = player.mediaId,
                isPlaying = player.isPlaying,
                isLoading = partial.isLoading,
                error = partial.error,
                showMiniPlayerForVideos = partial.showMiniPlayerForVideos,
                autoPlayNextMedia = partial.autoPlayNextMedia
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WearHomeUiState())
    }

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { repository.refreshMedia() }
    }

    fun moveItem(fromIndex: Int, toIndex: Int) {
        repository.move(fromIndex, toIndex)
    }
}

private data class PartialWearHomeState(
    val items: List<WearMediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showMiniPlayerForVideos: Boolean = true,
    val autoPlayNextMedia: Boolean = true
)
