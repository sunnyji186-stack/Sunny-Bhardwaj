package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Cinematic Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = CinemaAccent,
    secondary = CinemaAccentMuted,
    tertiary = CinemaTextSecondary,
    background = CinemaDark,
    surface = CinemaSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = CinemaTextPrimary,
    onSurface = CinemaTextPrimary,
    surfaceVariant = CinemaCard,
    onSurfaceVariant = CinemaTextPrimary
)

// We force the cinematic dark palette to ensure that the app feels like a true theater streaming console!
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force cinematic dark representation
    dynamicColor: Boolean = false, // Disable dynamic scheme to maintain standard Netflix/OTT branding fidelity
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

