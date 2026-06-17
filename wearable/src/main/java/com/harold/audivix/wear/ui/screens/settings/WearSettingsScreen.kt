package com.harold.audivix.wear.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Text
import com.harold.audivix.wear.ui.theme.wearBackground
import com.harold.audivix.wear.ui.theme.wearInputColor
import com.harold.audivix.wear.ui.theme.wearCardBorderColor
import com.harold.audivix.wear.ui.theme.wearButtonColor
import com.harold.audivix.wear.ui.theme.wearButtonTextColor
import com.harold.audivix.wear.ui.theme.wearCardColor
import com.harold.audivix.wear.ui.theme.wearTextColor
import com.harold.audivix.wear.viewmodel.WearSettingsViewModel

@Composable
fun WearSettingsScreen(
    viewModel: WearSettingsViewModel,
    darkTheme: Boolean,
    onBack: () -> Unit
) {
    val endpoint by viewModel.endpoint.collectAsState()
    val darkThemeSetting by viewModel.darkTheme.collectAsState()
    val showMiniPlayerForVideos by viewModel.showMiniPlayerForVideos.collectAsState()
    val autoPlayNextMedia by viewModel.autoPlayNextMedia.collectAsState()

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(wearBackground(darkTheme)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Settings",
                color = wearTextColor(darkTheme),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )
        }
        item {
            ToggleRow("Dark Theme", darkThemeSetting, darkTheme, viewModel::updateDarkTheme)
        }
        item {
            ToggleRow("Show mini player for videos", showMiniPlayerForVideos, darkTheme, viewModel::updateShowMiniPlayerForVideos)
        }
        item {
            ToggleRow("Auto play next media", autoPlayNextMedia, darkTheme, viewModel::updateAutoPlayNextMedia)
        }
        item {
            Card(
                onClick = {},
                colors = CardDefaults.cardColors(containerColor = wearCardColor(darkTheme)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .border(
                        width = if (darkTheme) 0.dp else 1.dp,
                        color = wearCardBorderColor(darkTheme),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Endpoint", color = wearTextColor(darkTheme))
                    BasicTextField(
                        value = endpoint,
                        onValueChange = viewModel::updateEndpoint,
                        singleLine = false,
                        textStyle = TextStyle(color = wearTextColor(darkTheme)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(wearInputColor(darkTheme), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    )
                }
            }
        }
        item {
            Button(
                onClick = onBack,
                modifier = Modifier.background(wearButtonColor(darkTheme), RoundedCornerShape(24.dp))
            ) {
                Text("Back", color = wearButtonTextColor(darkTheme))
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    darkTheme: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        onClick = { onCheckedChange(!checked) },
        colors = CardDefaults.cardColors(containerColor = wearCardColor(darkTheme)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .border(
                width = if (darkTheme) 0.dp else 1.dp,
                color = wearCardBorderColor(darkTheme),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = wearTextColor(darkTheme),
                modifier = Modifier.weight(1f)
            )
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
