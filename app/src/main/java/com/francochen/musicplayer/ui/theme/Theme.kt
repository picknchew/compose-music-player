package com.francochen.musicplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    onPrimary = Color.White,
    secondaryContainer = Color(0x004A77FF),
    inverseOnSurface = Color(0xF2F2F2FF),
    surfaceContainer = Color(0xFF2E3240),
    surfaceTint = Color(0xFF81C784)
)

private val LightColorScheme = lightColorScheme(
    onPrimary = Color.White,
    secondaryContainer = Color(0xFF004A77),
    inverseOnSurface = Color(0xFFF2F2F2),
    surfaceContainer = Color(0xFF2E3240),
    surfaceTint = Color(0xFF81C784)
)

@Composable
fun MusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}