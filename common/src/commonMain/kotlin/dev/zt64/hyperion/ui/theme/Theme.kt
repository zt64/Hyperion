package dev.zt64.hyperion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import dev.icerock.moko.resources.StringResource
import dev.zt64.hyperion.domain.model.StringLabel
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.LocalWindowSizeClass

enum class Theme(override val label: StringResource) : StringLabel {
    SYSTEM(MR.strings.system),
    LIGHT(MR.strings.light),
    DARK(MR.strings.dark)
}

@Composable
expect fun HyperionTheme(content: @Composable () -> Unit)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun HyperionTheme(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(colorScheme) {
        CompositionLocalProvider(
            // LocalScrollbarStyle provides ScrollbarStyle(
            //     minimalHeight = 16.dp,
            //     thickness = 12.dp,
            //     shape = CircleShape,
            //     hoverDurationMillis = 300,
            //     unhoverColor = colorScheme.secondary.copy(alpha = 0.5f),
            //     hoverColor = colorScheme.secondary.copy(alpha = 0.9f)
            // ),
            LocalWindowSizeClass provides calculateWindowSizeClass(),
            content = content
        )
    }
}

@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun systemColorScheme(): ColorScheme = if (isSystemInDarkTheme()) {
    darkColorScheme()
} else {
    lightColorScheme()
}