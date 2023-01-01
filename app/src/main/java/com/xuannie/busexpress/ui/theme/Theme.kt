package com.xuannie.busexpress.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = DarkNavyBlack,
    secondary = DarkPurpleBlack,
    background = NavyBlack,
    surface = DarkThemeSurface,
    onPrimary = PureWhite,
    onSurface = PureWhite,
    error = Red900,
    onError = PureWhite
)

private val LightColorPalette = lightColors(
    primary = BlueGrey700,
    secondary = BlueWhite,
    background = BlueGrey100,
    surface = LightBlueGrey,
    onPrimary = PureWhite,
    onSurface = PureWhite,
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
fun BusExpressTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}