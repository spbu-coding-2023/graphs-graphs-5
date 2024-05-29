package view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun Material3AppTheme(theme: Theme, content: @Composable () -> Unit) {
    val colors = when (theme) {
        Theme.SPECIAL -> DarkColorPalette
        else -> LightColorPalette
    }
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

enum class Theme {
    CLASSIC, SPECIAL
}