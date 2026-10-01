package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HazardOrange,
    onPrimary = Color.White,
    primaryContainer = HazardRedDark,
    onPrimaryContainer = Color.White,
    secondary = HazardBlue,
    onSecondary = Color.White,
    tertiary = HazardAmber,
    onTertiary = Color.Black,
    background = SlateDark900,
    onBackground = Color(0xFFF1F5F9),
    surface = SlateDark800,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = SlateDark700,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = SlateBorder,
    error = HazardRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = HazardOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEDD5),
    onPrimaryContainer = Color(0xFF7C2D12),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    tertiary = HazardAmber,
    onTertiary = Color.White,
    background = SlateLight100,
    onBackground = SlateLightText,
    surface = Color.White,
    onSurface = SlateLightText,
    surfaceVariant = SlateLight200,
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = HazardRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent high-contrast hazard styling
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Emergency apps shine in immersive dark mode
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
