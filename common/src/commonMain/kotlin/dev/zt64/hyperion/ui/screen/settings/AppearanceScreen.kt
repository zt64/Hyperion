package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.SUPPORTS_DYNAMIC_COLOR
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.ui.component.setting.RadioSetting
import dev.zt64.hyperion.ui.component.setting.SliderSetting
import dev.zt64.hyperion.ui.component.setting.SwitchSetting

context(ColumnScope)
@Composable
fun AppearanceScreen(preferences: PreferencesManager) {
    if (SUPPORTS_DYNAMIC_COLOR) {
        SwitchSetting(
            preference = preferences::dynamicColor,
            text = stringResource(MR.strings.dynamic_color),
            icon = Icons.Default.Palette
        )
    }

    RadioSetting(
        preference = preferences::theme,
        icon = Icons.Default.Style,
        label = stringResource(MR.strings.theme),
        description = stringResource(MR.strings.theme_setting_description),
    )

    SliderSetting(
        preference = preferences::timestampScale,
        text = stringResource(MR.strings.timestamp_scale),
        valueRange = 0.8f..2f,
    )

    SwitchSetting(
        preference = preferences::showDownloadButton,
        text = stringResource(MR.strings.show_download_button),
        icon = Icons.Default.Download,
    )

    SwitchSetting(
        preference = preferences::showRelatedVideos,
        text = stringResource(MR.strings.show_related_videos),
        icon = Icons.Default.List
    )

    SwitchSetting(
        preference = preferences::hideNavItemLabel,
        text = stringResource(MR.strings.hide_nav_item_label),
        icon = Icons.Default.Navigation
    )
}