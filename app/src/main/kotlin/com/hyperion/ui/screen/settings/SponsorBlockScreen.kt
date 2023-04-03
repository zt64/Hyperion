package com.hyperion.ui.screen.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.component.setting.SwitchSetting

context(ColumnScope)
@Composable
fun SponsorBlockScreen(
    preferencesManager: PreferencesManager
) {
    SwitchSetting(
        checked = preferencesManager.sponsorBlockEnabled,
        text = stringResource(R.string.enabled),
        onCheckedChange = { preferencesManager.sponsorBlockEnabled = it }
    )
}