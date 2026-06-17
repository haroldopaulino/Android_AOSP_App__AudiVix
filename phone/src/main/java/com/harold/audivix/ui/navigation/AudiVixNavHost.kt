package com.harold.audivix.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harold.audivix.data.repository.AppContainer
import com.harold.audivix.ui.screens.home.HomeScreen
import com.harold.audivix.ui.screens.settings.SettingsScreen
import com.harold.audivix.viewmodel.HomeViewModel
import com.harold.audivix.viewmodel.HomeViewModelFactory
import com.harold.audivix.viewmodel.SettingsViewModel
import com.harold.audivix.viewmodel.SettingsViewModelFactory

@Composable
fun AudiVixNavHost(container: AppContainer) {
    var route by rememberSaveable { mutableStateOf(BottomRoute.Home) }
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(container.mediaRepository))
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(container.settingsRepository, container.mediaRepository))

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = route == BottomRoute.Home,
                    onClick = { route = BottomRoute.Home },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = route == BottomRoute.Settings,
                    onClick = { route = BottomRoute.Settings },
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        when (route) {
            BottomRoute.Home -> HomeScreen(homeViewModel, container.playerController, Modifier.padding(padding))
            BottomRoute.Settings -> SettingsScreen(settingsViewModel, Modifier.padding(padding))
        }
    }
}
