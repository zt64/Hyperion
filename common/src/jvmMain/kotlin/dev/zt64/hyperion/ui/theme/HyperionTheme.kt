package dev.zt64.hyperion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import dev.zt64.hyperion.domain.manager.PreferencesManager
import org.koin.compose.koinInject

@Composable
actual fun HyperionTheme(content: @Composable () -> Unit) {
    val preferences: PreferencesManager = koinInject()

    val colorScheme = if (isSystemInDarkTheme() &&
        preferences.theme == Theme.SYSTEM ||
        preferences.theme == Theme.DARK
    ) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    HyperionTheme(
        colorScheme = colorScheme,
        content = content
    )
}