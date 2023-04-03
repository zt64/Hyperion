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
import com.hyperion.ui.theme.Theme
import kotlinx.collections.immutable.toImmutableMap

context(ColumnScope)
@Composable
fun AppearanceScreen(preferences: PreferencesManager) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        SwitchSetting(
            checked = preferences.dynamicColor,
            text = stringResource(R.string.dynamic_color),
            icon = Icons.Default.Palette,
            onCheckedChange = { preferences.dynamicColor = it }
        )
    }

    RadioSetting(
        icon = Icons.Default.Style,
        label = stringResource(R.string.theme),
        description = stringResource(R.string.theme_setting_description),
        value = preferences.theme,
        options = Theme.values().associateBy { theme -> stringResource(theme.displayName) }.toImmutableMap(),
        onConfirm = { preferences.theme = it }
    )

    SwitchSetting(
        checked = preferences.compactCard,
        text = stringResource(R.string.compact_card),
        icon = Icons.Default.VideoSettings,
        onCheckedChange = { preferences.compactCard = it }
    )

    SliderSetting(
        text = stringResource(R.string.timestamp_scale),
        value = preferences.timestampScale,
        valueRange = 0.8f..2f,
        onValueChangeFinished = { preferences.timestampScale = it }
    )

    SwitchSetting(
        checked = preferences.showDownloadButton,
        text = stringResource(R.string.show_download_button),
        icon = Icons.Default.Download,
        onCheckedChange = { preferences.showDownloadButton = it }
    )

    SwitchSetting(
        checked = preferences.showRelatedVideos,
        text = stringResource(R.string.show_related_videos),
        icon = Icons.Default.List,
        onCheckedChange = { preferences.showRelatedVideos = it }
    )

    SwitchSetting(
        checked = preferences.hideNavItemLabel,
        text = stringResource(R.string.hide_nav_item_label),
        icon = Icons.Default.Navigation,
        onCheckedChange = { preferences.hideNavItemLabel = it }
    )
}