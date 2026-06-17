package com.harold.audivix.data.repository

import android.content.Context
import com.harold.audivix.data.local.AudiVixDatabase
import com.harold.audivix.data.model.AUDIVIX_DEFAULT_ENDPOINT
import com.harold.audivix.data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class SettingsRepository(context: Context, private val database: AudiVixDatabase) {
    private val _settings = MutableStateFlow(database.readSettings(AUDIVIX_DEFAULT_ENDPOINT))
    val settings: StateFlow<AppSettings> = _settings

    suspend fun updateEndpoint(endpoint: String) = update { it.copy(endpoint = endpoint) }
    suspend fun updateDarkTheme(enabled: Boolean) = update { it.copy(darkTheme = enabled) }
    suspend fun updateNotifications(enabled: Boolean) = update { it.copy(notificationsEnabled = enabled) }
    suspend fun updateChimes(enabled: Boolean) = update { it.copy(chimesEnabled = enabled) }
    suspend fun updateOfflineDownloads(enabled: Boolean) = update { it.copy(offlineDownloadsEnabled = enabled) }

    private suspend fun update(block: (AppSettings) -> AppSettings) {
        withContext(Dispatchers.IO) {
            val newSettings = block(_settings.value)
            database.saveSettings(newSettings)
            _settings.value = newSettings
        }
    }

    suspend fun setShowMiniPlayerForVideos(enabled: Boolean) = update { it.copy(showMiniPlayerForVideos = enabled) }
    suspend fun setAutoPlayNextMedia(enabled: Boolean) = update { it.copy(autoPlayNextMedia = enabled) }
}
