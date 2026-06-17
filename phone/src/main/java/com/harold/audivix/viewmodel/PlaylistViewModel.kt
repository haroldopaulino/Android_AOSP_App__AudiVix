package com.harold.audivix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harold.audivix.data.model.AudiVixPlaylist
import com.harold.audivix.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PlaylistUiState(
    val loading: Boolean = true,
    val playlists: List<AudiVixPlaylist> = emptyList(),
    val error: String? = null
)

class PlaylistViewModel(private val repository: MediaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistUiState())
    val uiState: StateFlow<PlaylistUiState> = _uiState

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            runCatching { repository.getPlaylists() }
                .onSuccess { _uiState.value = PlaylistUiState(loading = false, playlists = it) }
                .onFailure { _uiState.value = PlaylistUiState(loading = false, error = it.message) }
        }
    }

    fun save(name: String, itemIds: List<String>) {
        viewModelScope.launch {
            runCatching { repository.savePlaylist(name, itemIds) }
            refresh()
        }
    }
}
