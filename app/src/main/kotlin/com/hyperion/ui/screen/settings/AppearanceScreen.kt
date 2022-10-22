package com.hyperion.ui.screen.settings

import android.os.Build
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.ui.component.setting.RadioSetting
import com.hyperion.ui.component.setting.SliderSetting
import com.hyperion.ui.component.setting.SwitchSetting
import com.hyperion.ui.theme.Theme
import com.hyperion.ui.viewmodel.SettingsViewModel

context(ColumnScope)
@Composable
fun AppearanceScreen(
    viewModel: SettingsViewModel
) {
    val preferences = viewModel.preferences

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
        options = Theme.values().associateBy { theme -> stringResource(theme.displayName) },
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
}