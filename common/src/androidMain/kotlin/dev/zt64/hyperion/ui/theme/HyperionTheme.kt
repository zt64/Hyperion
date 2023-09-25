package dev.zt64.hyperion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.zt64.hyperion.SUPPORTS_DYNAMIC_COLOR

@Composable
actual fun HyperionTheme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    HyperionTheme(
        isDarkTheme = isDarkTheme,
        isDynamicColor = false,
        content = content
    )
}

@Composable
fun HyperionTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val dynamicColor = isDynamicColor && SUPPORTS_DYNAMIC_COLOR

    @Suppress("NewApi")
    val colorScheme = when {
        dynamicColor && isDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !isDarkTheme -> dynamicLightColorScheme(LocalContext.current)
        isDarkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}