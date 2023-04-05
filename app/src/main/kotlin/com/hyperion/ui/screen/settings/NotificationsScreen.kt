package com.hyperion.ui.screen.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.component.setting.SwitchSetting

context(ColumnScope)
@Composable
fun NotificationsScreen(preferencesManager: PreferencesManager) {
    SwitchSetting(
        checked = false,
        text = stringResource(R.string.subscriptions),
        description = stringResource(R.string.subscriptions_notification_desc),
        onCheckedChange = {}
    )

    SwitchSetting(
        checked = false,
        text = stringResource(R.string.replies_to_my_comments),
        description = stringResource(R.string.reply_notification_desc),
        onCheckedChange = {}
    )

    SwitchSetting(
        checked = false,
        text = stringResource(R.string.mentions),
        description = stringResource(R.string.mention_notification_desc),
        onCheckedChange = {}
    )

    SwitchSetting(
        checked = false,
        text = stringResource(R.string.shared_content),
        description = stringResource(R.string.shared_content_notification_desc),
        onCheckedChange = {}
    )
}