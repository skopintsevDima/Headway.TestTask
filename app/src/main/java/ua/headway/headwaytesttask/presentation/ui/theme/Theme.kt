package ua.headway.headwaytesttask.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import ua.headway.booksummary.presentation.ui.resources.LocalResources

private val DarkColorPalette = darkColors(
    primary = LocalResources.Colors.Blue,
    secondary = LocalResources.Colors.White,
    surface = LocalResources.Colors.MilkWhite,
    onSurface = LocalResources.Colors.Black,
    background = LocalResources.Colors.DarkGray,
    onBackground = LocalResources.Colors.MilkWhite,
)

private val LightColorPalette = lightColors(
    primary = LocalResources.Colors.Blue,
    secondary = LocalResources.Colors.Black,
    surface = LocalResources.Colors.LightGray,
    onSurface = LocalResources.Colors.Black,
    background = LocalResources.Colors.MilkWhite,
    onBackground = LocalResources.Colors.Gray,
)

@Composable
fun HeadwayTestTaskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}