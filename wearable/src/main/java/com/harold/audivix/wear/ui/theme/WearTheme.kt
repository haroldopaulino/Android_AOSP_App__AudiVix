package com.harold.audivix.wear.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.MaterialTheme

val WearDarkBackground = Color(0xFF050806)
val WearLightBackground = Color(0xFFF7FFF9)
val WearDarkCard = Color(0xFF111A13)
val WearLightCard = Color(0xFFFFFFFF)
val WearLightCardBorder = Color(0xFFBFDCC8)
val WearDarkText = Color(0xFFEAF7EA)
val WearLightText = Color(0xFF0B2414)
val WearLightSubText = Color(0xFF516858)
val WearAccent = Color(0xFF20D86B)
val WearLightAccent = Color(0xFF087F3C)
val WearButtonLight = Color(0xFFDDF5E4)
val WearButtonLightText = Color(0xFF073C1D)
val WearInputLight = Color(0xFFF0FBF3)

@Composable
fun AudiVixWearTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(content = content)
}

fun wearBackground(darkTheme: Boolean): Color = if (darkTheme) WearDarkBackground else WearLightBackground
fun wearCardColor(darkTheme: Boolean): Color = if (darkTheme) WearDarkCard else WearLightCard
fun wearCardBorderColor(darkTheme: Boolean): Color = if (darkTheme) Color.Transparent else WearLightCardBorder
fun wearTextColor(darkTheme: Boolean): Color = if (darkTheme) WearDarkText else WearLightText
fun wearSecondaryTextColor(darkTheme: Boolean): Color = if (darkTheme) WearDarkText.copy(alpha = 0.74f) else WearLightSubText
fun wearAccentColor(darkTheme: Boolean): Color = if (darkTheme) WearAccent else WearLightAccent
fun wearButtonColor(darkTheme: Boolean): Color = if (darkTheme) WearAccent else WearButtonLight
fun wearButtonTextColor(darkTheme: Boolean): Color = if (darkTheme) Color(0xFF001F10) else WearButtonLightText
fun wearInputColor(darkTheme: Boolean): Color = if (darkTheme) Color(0xFF101A13) else WearInputLight
