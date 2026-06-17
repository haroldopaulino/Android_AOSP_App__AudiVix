package com.harold.audivix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harold.audivix.data.model.AppSettings
import com.harold.audivix.data.model.MediaType
import com.harold.audivix.data.repository.MediaRepository
import com.harold.audivix.data.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository, private val mediaRepository: MediaRepository? = null) : ViewModel() {
    val settings: StateFlow<AppSettings> = settingsRepository.settings

    fun setEndpoint(endpoint: String) = viewModelScope.launch { settingsRepository.updateEndpoint(endpoint) }
    fun setDarkTheme(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateDarkTheme(enabled) }
    fun setNotifications(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateNotifications(enabled) }
    fun setChimes(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateChimes(enabled) }
    fun setOfflineDownloads(enabled: Boolean) = viewModelScope.launch {
        settingsRepository.updateOfflineDownloads(enabled)
        if (enabled && mediaRepository != null) {
            val allMedia = mediaRepository.getCatalog(MediaType.Audio) + mediaRepository.getCatalog(MediaType.Video)
            mediaRepository.downloadAll(allMedia)
        }
    }

    fun setShowMiniPlayerForVideos(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowMiniPlayerForVideos(enabled)
        }
    }

    fun setAutoPlayNextMedia(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoPlayNextMedia(enabled)
        }
    }
}
