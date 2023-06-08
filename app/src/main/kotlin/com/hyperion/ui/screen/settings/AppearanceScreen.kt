package com.hyperion.ui.screen.settings

import android.os.Build
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.component.setting.RadioSetting
import com.hyperion.ui.component.setting.SliderSetting
import com.hyperion.ui.component.setting.SwitchSetting

context(ColumnScope)
@Composable
fun AppearanceScreen(preferences: PreferencesManager) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        SwitchSetting(
            preference = preferences::dynamicColor,
            text = stringResource(R.string.dynamic_color),
            icon = Icons.Default.Palette
        )
    }

    RadioSetting(
        preference = preferences::theme,
        icon = Icons.Default.Style,
        label = stringResource(R.string.theme),
        description = stringResource(R.string.theme_setting_description),
    )

    SwitchSetting(
        preference = preferences::compactCard,
        text = stringResource(R.string.compact_card),
        icon = Icons.Default.VideoSettings
    )

    SliderSetting(
        preference = preferences::timestampScale,
        text = stringResource(R.string.timestamp_scale),
        valueRange = 0.8f..2f,
    )

    SwitchSetting(
        preference = preferences::showDownloadButton,
        text = stringResource(R.string.show_download_button),
        icon = Icons.Default.Download,
    )

    SwitchSetting(
        preference = preferences::showRelatedVideos,
        text = stringResource(R.string.show_related_videos),
        icon = Icons.Default.List
    )

    SwitchSetting(
        preference = preferences::hideNavItemLabel,
        text = stringResource(R.string.hide_nav_item_label),
        icon = Icons.Default.Navigation
    )
}