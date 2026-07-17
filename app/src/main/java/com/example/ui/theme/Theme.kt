package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme
    get() = darkColorScheme(
        primary = SpotifyGreen,
        secondary = SpotifyLightGreen,
        tertiary = SpotifyVolt,
        background = SpotifyBlack,
        surface = SpotifyDarkGray,
        surfaceVariant = SpotifyCardGray,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onTertiary = Color.Black,
        onBackground = SpotifyTextWhite,
        onSurface = SpotifyTextWhite,
        onSurfaceVariant = SpotifyTextGray,
        error = Color(0xFFCF6679)
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
