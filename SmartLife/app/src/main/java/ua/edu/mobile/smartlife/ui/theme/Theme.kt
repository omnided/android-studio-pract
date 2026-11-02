package ua.edu.mobile.smartlife.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Green40,
    primaryContainer = Green90,
    secondary = GreenGrey40,
    tertiary = Teal40,
)

private val DarkColors = darkColorScheme(
    primary = Green80,
    primaryContainer = Green30,
    secondary = GreenGrey80,
    tertiary = Teal80,
)

@Composable
fun SmartLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
