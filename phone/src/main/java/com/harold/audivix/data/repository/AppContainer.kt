package com.harold.audivix.data.repository

import android.content.Context
import android.provider.Settings
import com.harold.audivix.data.local.AudiVixDatabase
import com.harold.audivix.player.PlayerController

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = AudiVixDatabase(appContext)
    private val deviceId = Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID) ?: "android-audivix-device"
    val settingsRepository = SettingsRepository(appContext, database)
    val cacheRepository = CacheRepository(appContext, database)
    val mediaRepository = MediaRepository(settingsRepository, cacheRepository, database, deviceId)
    val playerController = PlayerController(appContext)
}
