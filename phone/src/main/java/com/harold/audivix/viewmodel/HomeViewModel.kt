package com.harold.audivix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harold.audivix.data.model.AudiVixMedia
import com.harold.audivix.data.model.MediaType
import com.harold.audivix.data.repository.MediaRepository
import com.harold.audivix.player.PlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val audio: List<AudiVixMedia> = emptyList(),
    val video: List<AudiVixMedia> = emptyList(),
    val selectedType: MediaType = MediaType.Audio,
    val preparingId: String? = null
) {
    val visibleCatalog: List<AudiVixMedia> = if (selectedType == MediaType.Audio) audio else video
}

class HomeViewModel(private val repository: MediaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            runCatching {
                val audio = repository.getCatalog(MediaType.Audio)
                val video = repository.getCatalog(MediaType.Video)
                audio to video
            }.onSuccess { (audio, video) ->
                _uiState.update { it.copy(loading = false, audio = audio, video = video) }
            }.onFailure { error ->
                _uiState.update { it.copy(loading = false, error = error.message ?: "Unable to load media") }
            }
        }
    }

    fun selectType(type: MediaType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun moveDraggedItem(itemId: String, direction: Int) {
        val state = _uiState.value
        val list = state.visibleCatalog.toMutableList()
        val fromIndex = list.indexOfFirst { it.id == itemId }
        if (fromIndex == -1) return
        val toIndex = (fromIndex + direction).coerceIn(0, list.lastIndex)
        if (fromIndex == toIndex) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        if (state.selectedType == MediaType.Audio) {
            _uiState.update { it.copy(audio = list) }
        } else {
            _uiState.update { it.copy(video = list) }
        }
        viewModelScope.launch { repository.saveOrder(state.selectedType, list.map { it.id }) }
    }

    fun playFirstAudioIfIdle(controller: PlayerController) {
        if (controller.state.value.current != null || _uiState.value.audio.isEmpty()) return
        play(_uiState.value.audio.first(), controller)
    }

    fun play(media: AudiVixMedia, controller: PlayerController) {
        if (controller.state.value.current?.id == media.id) {
            controller.toggle()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(preparingId = media.id) }
            val prepared = repository.prepareForPlayback(media, downloadMedia = true)
            val state = _uiState.value
            val queue = state.visibleCatalog.map { if (it.id == prepared.id) prepared else it }
            controller.play(prepared, queue) { next -> preload(next) }
            replacePrepared(prepared)
            _uiState.update { it.copy(preparingId = null) }
        }
    }

    fun preload(media: AudiVixMedia) {
        viewModelScope.launch { repository.preloadNext(media) }
    }

    fun downloadAllVisible() {
        viewModelScope.launch { repository.downloadAll(_uiState.value.visibleCatalog) }
    }

    private fun replacePrepared(media: AudiVixMedia) {
        _uiState.update { state ->
            if (media.type == MediaType.Audio) state.copy(audio = state.audio.map { if (it.id == media.id) media else it })
            else state.copy(video = state.video.map { if (it.id == media.id) media else it })
        }
    }
}
