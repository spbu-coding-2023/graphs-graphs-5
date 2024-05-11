package view

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun Material3AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = when {
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}