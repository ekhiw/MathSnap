package xyz.potasyyum.mathsnap.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import xyz.potasyyum.mathsnap.BuildConfig

private val DarkColorPalette = darkColors(
    primary = if (BuildConfig.BASE_THEME == "green") tailwindColors().green200 else tailwindColors().red200,
    primaryVariant = if (BuildConfig.BASE_THEME == "green") tailwindColors().green700 else tailwindColors().red700,
    secondary = if (BuildConfig.BASE_THEME == "green") tailwindColors().green800 else tailwindColors().red800
)

private val LightColorPalette = lightColors(
    primary = if (BuildConfig.BASE_THEME == "green") tailwindColors().green500 else tailwindColors().red500,
    primaryVariant = if (BuildConfig.BASE_THEME == "green") tailwindColors().green700 else tailwindColors().red700,
    secondary = if (BuildConfig.BASE_THEME == "green") tailwindColors().green800 else tailwindColors().red800

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
fun MathSnapTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
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