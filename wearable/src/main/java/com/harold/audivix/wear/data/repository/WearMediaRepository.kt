package com.harold.audivix.wear.data.repository

import com.harold.audivix.wear.data.api.WearApiFactory
import com.harold.audivix.wear.data.model.AUDIVIX_WEAR_ENDPOINT
import com.harold.audivix.wear.data.model.WearMediaItem
import com.harold.audivix.wear.data.model.toWearMedia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class WearMediaRepository {
    private val endpoint = MutableStateFlow(AUDIVIX_WEAR_ENDPOINT)
    private val darkTheme = MutableStateFlow(true)
    private val notificationsEnabled = MutableStateFlow(true)
    private val showMiniPlayerForVideos = MutableStateFlow(true)
    private val autoPlayNextMedia = MutableStateFlow(true)
    private val media = MutableStateFlow<List<WearMediaItem>>(emptyList())
    private val loading = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    fun endpoint(): StateFlow<String> = endpoint
    fun darkTheme(): StateFlow<Boolean> = darkTheme
    fun notificationsEnabled(): StateFlow<Boolean> = notificationsEnabled
    fun showMiniPlayerForVideos(): StateFlow<Boolean> = showMiniPlayerForVideos
    fun autoPlayNextMedia(): StateFlow<Boolean> = autoPlayNextMedia
    fun media(): StateFlow<List<WearMediaItem>> = media
    fun loading(): StateFlow<Boolean> = loading
    fun error(): StateFlow<String?> = error

    suspend fun refreshMedia() {
        loading.value = true
        error.value = null
        try {
            media.value = WearApiFactory.create(endpoint.value)
                .getMedia()
                .allItems()
                .map { it.toWearMedia(endpoint.value) }
        } catch (throwable: Throwable) {
            error.value = throwable.message ?: "Unable to load media"
        } finally {
            loading.value = false
        }
    }

    fun updateEndpoint(value: String) {
        endpoint.value = value
    }

    fun updateDarkTheme(enabled: Boolean) {
        darkTheme.value = enabled
    }

    fun updateNotifications(enabled: Boolean) {
        notificationsEnabled.value = enabled
    }

    fun updateShowMiniPlayerForVideos(enabled: Boolean) {
        showMiniPlayerForVideos.value = enabled
    }

    fun updateAutoPlayNextMedia(enabled: Boolean) {
        autoPlayNextMedia.value = enabled
    }

    fun move(fromIndex: Int, toIndex: Int) {
        media.update { current ->
            if (fromIndex !in current.indices || toIndex !in current.indices) {
                current
            } else {
                current.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
            }
        }
    }
}
