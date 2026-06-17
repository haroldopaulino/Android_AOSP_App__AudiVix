package com.harold.audivix

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.harold.audivix.ui.navigation.AudiVixNavHost
import com.harold.audivix.ui.theme.AudiVixTheme
import com.harold.audivix.viewmodel.SettingsViewModel
import com.harold.audivix.viewmodel.SettingsViewModelFactory

class MainActivity : ComponentActivity() {
    private val container by lazy { (application as AudiVixApp).container }
    private val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModelFactory(container.settingsRepository, container.mediaRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            AudiVixTheme(darkTheme = settings.darkTheme) {
                AudiVixNavHost(container)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }
}
