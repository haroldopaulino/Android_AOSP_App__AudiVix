package com.harold.audivix.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harold.audivix.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val settings by viewModel.settings.collectAsState()
    var endpoint by remember(settings.endpoint) { mutableStateOf(settings.endpoint) }
    Column(modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Settings", fontWeight = FontWeight.Bold) })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text("Appearance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            SettingRow("Dark theme", "Switch between dark and light mode") {
                Switch(checked = settings.darkTheme, onCheckedChange = viewModel::setDarkTheme)
            }
            Text("Backend", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = endpoint,
                onValueChange = {
                    endpoint = it
                    viewModel.setEndpoint(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Endpoint") },
                singleLine = true
            )
            Text("Playback", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            SettingRow("Chimes", "Play app feedback sounds when available") {
                Switch(checked = settings.chimesEnabled, onCheckedChange = viewModel::setChimes)
            }
            SettingRow("Pre-download all media", "Download audio and video for offline playback") {
                Checkbox(checked = settings.offlineDownloadsEnabled, onCheckedChange = viewModel::setOfflineDownloads)
            }
            Text("About", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("AudiVix streams audio and video from your private backend. Media availability and royalty-free licensing are managed by the server content library.", style = MaterialTheme.typography.bodyMedium)

            SettingRow("Show mini player for videos", "Display a small video player below the active video item") {
                Switch(
                    checked = settings.showMiniPlayerForVideos,
                    onCheckedChange = viewModel::setShowMiniPlayerForVideos
                )
            }

            SettingRow("Auto play next media", "Start the next item automatically when playback finishes") {
                Switch(
                    checked = settings.autoPlayNextMedia,
                    onCheckedChange = viewModel::setAutoPlayNextMedia
                )
            }

        }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, trailing: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailing()
    }
}
