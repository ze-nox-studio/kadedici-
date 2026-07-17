package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String) {
    SPOTIFY_DARK("Spotify Dark"),
    SOLARIZED_AMBER("Solarized Amber"),
    AMETHYST_PURPLE("Amethyst Purple"),
    MIDNIGHT_BLUE("Midnight Blue"),
    SAKURA_ROSE("Sakura Rose")
}

var currentAppTheme by mutableStateOf(AppTheme.SPOTIFY_DARK)

val SpotifyGreen: Color
    get() = when (currentAppTheme) {
        AppTheme.SPOTIFY_DARK -> Color(0xFF1DB954)
        AppTheme.SOLARIZED_AMBER -> Color(0xFFF59E0B) // Bright Amber
        AppTheme.AMETHYST_PURPLE -> Color(0xFF9333EA) // Purple
        AppTheme.MIDNIGHT_BLUE -> Color(0xFF3B82F6) // Electric Blue
        AppTheme.SAKURA_ROSE -> Color(0xFFEC4899) // Vivid Pink
    }

val SpotifyLightGreen: Color
    get() = when (currentAppTheme) {
        AppTheme.SPOTIFY_DARK -> Color(0xFF1ED760)
        AppTheme.SOLARIZED_AMBER -> Color(0xFFFBBF24)
        AppTheme.AMETHYST_PURPLE -> Color(0xFFA855F7)
        AppTheme.MIDNIGHT_BLUE -> Color(0xFF60A5FA)
        AppTheme.SAKURA_ROSE -> Color(0xFFF472B6)
    }

val SpotifyBlack: Color
    get() = when (currentAppTheme) {
        AppTheme.SPOTIFY_DARK -> Color(0xFF0C0D0E)
        AppTheme.SOLARIZED_AMBER -> Color(0xFF0F0B07) // Warm Espresso Black
        AppTheme.AMETHYST_PURPLE -> Color(0xFF0D0B12) // Rich Obsidian Purple
        AppTheme.MIDNIGHT_BLUE -> Color(0xFF060A13) // Ocean Deep Black
        AppTheme.SAKURA_ROSE -> Color(0xFF0E070A) // Rosewood Dark Black
    }

val SpotifyDarkGray: Color
    get() = when (currentAppTheme) {
        AppTheme.SPOTIFY_DARK -> Color(0xFF121315)
        AppTheme.SOLARIZED_AMBER -> Color(0xFF1A140F)
        AppTheme.AMETHYST_PURPLE -> Color(0xFF14101A)
        AppTheme.MIDNIGHT_BLUE -> Color(0xFF0B101B)
        AppTheme.SAKURA_ROSE -> Color(0xFF180D13)
    }

val SpotifyCardGray: Color
    get() = when (currentAppTheme) {
        AppTheme.SPOTIFY_DARK -> Color(0xFF1C1E22)
        AppTheme.SOLARIZED_AMBER -> Color(0xFF2E221A)
        AppTheme.AMETHYST_PURPLE -> Color(0xFF231D2E)
        AppTheme.MIDNIGHT_BLUE -> Color(0xFF131D2F)
        AppTheme.SAKURA_ROSE -> Color(0xFF2B1723)
    }

val SpotifyVolt: Color
    get() = when (currentAppTheme) {
        AppTheme.SPOTIFY_DARK -> Color(0xFFCFFF04)
        AppTheme.SOLARIZED_AMBER -> Color(0xFFFBBF24)
        AppTheme.AMETHYST_PURPLE -> Color(0xFFE9D5FF)
        AppTheme.MIDNIGHT_BLUE -> Color(0xFF22D3EE)
        AppTheme.SAKURA_ROSE -> Color(0xFFFDA4AF)
    }

val SpotifyTextWhite = Color(0xFFFFFFFF)
val SpotifyTextGray = Color(0xFF9CA3AF)
val SpotifyTeal = Color(0xFF00D2FF)
val SpotifyPink = Color(0xFFFF007F)
val SpotifyYellow = Color(0xFFFFCC00)
val SpotifyBlackReal = Color(0xFF000000)
