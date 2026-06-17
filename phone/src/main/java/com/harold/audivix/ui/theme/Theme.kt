package com.harold.audivix.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB8A7FF),
    secondary = Color(0xFF7CE4CF),
    tertiary = Color(0xFFFFB86B),
    background = Color(0xFF101018),
    surface = Color(0xFF171724),
    surfaceVariant = Color(0xFF252538),
    primaryContainer = Color(0xFF342B61)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF5546A3),
    secondary = Color(0xFF006B5A),
    tertiary = Color(0xFF8C4B00),
    background = Color(0xFFFDFCFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE7E1F7),
    primaryContainer = Color(0xFFE8DDFF)
)

@Composable
fun AudiVixTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}
