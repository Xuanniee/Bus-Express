package com.xuannie.busexpress.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = DarkCerulean,
    secondary = QueenBlue,
    background = DarkMidnightBlue,
    surface = DarkCerulean,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = PureWhite,
    onSurface = PureWhite,
    surfaceVariant = BdazzledBlue,
    onSurfaceVariant = PureWhite,
    error = Red900,
    onError = PureWhite
)

private val LightColorPalette = lightColorScheme(
    primary = DarkCerulean,
    secondary = Rackley,
    background = AirSuperiorityBlueLight,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark,
    surfaceVariant = AirSuperiorityBlue,
    onSurfaceVariant = TextDark,
    error = Red900,
    onError = PureWhite

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun BusExpressTheme(
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
        shapes = Shapes,
        content = content
    )
}