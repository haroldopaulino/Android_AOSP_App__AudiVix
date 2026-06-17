package com.harold.audivix.wear.viewmodel

import androidx.lifecycle.ViewModel
import com.harold.audivix.wear.data.repository.WearMediaRepository
import kotlinx.coroutines.flow.StateFlow

class WearSettingsViewModel(private val repository: WearMediaRepository) : ViewModel() {
    val endpoint: StateFlow<String> = repository.endpoint()
    val darkTheme: StateFlow<Boolean> = repository.darkTheme()
    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled()
    val showMiniPlayerForVideos: StateFlow<Boolean> = repository.showMiniPlayerForVideos()
    val autoPlayNextMedia: StateFlow<Boolean> = repository.autoPlayNextMedia()

    fun updateEndpoint(value: String) = repository.updateEndpoint(value)
    fun updateDarkTheme(enabled: Boolean) = repository.updateDarkTheme(enabled)
    fun updateNotifications(enabled: Boolean) = repository.updateNotifications(enabled)
    fun updateShowMiniPlayerForVideos(enabled: Boolean) = repository.updateShowMiniPlayerForVideos(enabled)
    fun updateAutoPlayNextMedia(enabled: Boolean) = repository.updateAutoPlayNextMedia(enabled)
}
