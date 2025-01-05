package com.example.weatherapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

private val LightColorPalette = lightColorScheme(
    primary = Color(0xFF403C73),
    onPrimary = Color.White,

    secondary = Color(0xFF424155),
    onSecondary = Color.White,

    tertiary = Color(0xFF5c384b),
    onTertiary = Color.White,

    primaryContainer = Color(0xFF726ea9),
    onPrimaryContainer = Color.White,

    secondaryContainer = Color(0xFF757288),
    onSecondaryContainer = Color.White,

    tertiaryContainer = Color(0xFF93687d),
    onTertiaryContainer = Color.White,

    // background = Color(0xFFFFFFFF),

    // surface = Color(0xFFfcf8ff),
    inverseSurface = Color(0xFF313036),
    // surfaceDim = Color(0xFFdcd8e0),
    onSurface = Color.White,

    error = Color(0xFF8c0009),
    onError = Color.White,
)

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes(),
        content = content
    )
}