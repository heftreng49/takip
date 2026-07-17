package com.instagram.unfollowers.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Instagram-inspired palette
val InstagramPurple = Color(0xFF833AB4)
val InstagramPink = Color(0xFFE1306C)
val InstagramOrange = Color(0xFFF77737)
val InstagramYellow = Color(0xFFFCAF45)
val GradientStart = Color(0xFF405DE6)
val GradientEnd = Color(0xFFE1306C)

val SurfaceDark = Color(0xFF1A1A1A)
val SurfaceLight = Color(0xFFF8F8F8)
val CardDark = Color(0xFF242424)
val CardLight = Color(0xFFFFFFFF)

private val DarkColors = darkColorScheme(
    primary = InstagramPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF5C1A32),
    onPrimaryContainer = Color(0xFFFFB3C1),
    secondary = InstagramPurple,
    onSecondary = Color.White,
    background = SurfaceDark,
    onBackground = Color(0xFFE8E8E8),
    surface = CardDark,
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0xFF2E2E2E),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = Color(0xFF3D3D3D),
    error = Color(0xFFFF6B6B),
)

private val LightColors = lightColorScheme(
    primary = InstagramPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD6E0),
    onPrimaryContainer = Color(0xFF5C1A32),
    secondary = InstagramPurple,
    onSecondary = Color.White,
    background = SurfaceLight,
    onBackground = Color(0xFF1A1A1A),
    surface = CardLight,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF555555),
    outline = Color(0xFFDDDDDD),
    error = Color(0xFFD32F2F),
)

@Composable
fun InstagramUnfollowersTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
