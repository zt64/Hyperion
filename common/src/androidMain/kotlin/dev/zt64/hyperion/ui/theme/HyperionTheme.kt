package dev.zt64.hyperion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.zt64.hyperion.SUPPORTS_DYNAMIC_COLOR
import dev.zt64.hyperion.domain.manager.PreferencesManager
import org.koin.compose.koinInject

@Composable
actual fun HyperionTheme(content: @Composable () -> Unit) {
    val preferences: PreferencesManager = koinInject()

    val isDarkTheme = isSystemInDarkTheme() &&
        preferences.theme == Theme.SYSTEM ||
        preferences.theme == Theme.DARK

    val colorScheme = if (SUPPORTS_DYNAMIC_COLOR && preferences.dynamicColor) {
        val context = LocalContext.current

        if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (isDarkTheme) darkColorScheme() else lightColorScheme()
    }

    HyperionTheme(
        colorScheme = colorScheme,
        content = content
    )
}