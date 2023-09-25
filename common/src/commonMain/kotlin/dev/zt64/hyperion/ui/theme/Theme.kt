package dev.zt64.hyperion.ui.theme;

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.domain.model.StringLabel

enum class Theme(
    override val label: StringResource
) : StringLabel {
    SYSTEM(MR.strings.system),
    LIGHT(MR.strings.light),
    DARK(MR.strings.dark);
}

@Composable
expect fun HyperionTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
)