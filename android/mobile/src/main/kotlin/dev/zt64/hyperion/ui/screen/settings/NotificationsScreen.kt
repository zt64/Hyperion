package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.ui.component.setting.SwitchSetting

context(ColumnScope)
@Composable
fun NotificationsScreen(preferencesManager: PreferencesManager) {
    SwitchSetting(
        checked = false,
        text = stringResource(MR.strings.subscriptions),
        description = stringResource(MR.strings.subscriptions_notification_desc),
        onCheckedChange = {}
    )

    SwitchSetting(
        checked = false,
        text = stringResource(MR.strings.replies_to_my_comments),
        description = stringResource(MR.strings.reply_notification_desc),
        onCheckedChange = {}
    )

    SwitchSetting(
        checked = false,
        text = stringResource(MR.strings.mentions),
        description = stringResource(MR.strings.mention_notification_desc),
        onCheckedChange = {}
    )

    SwitchSetting(
        checked = false,
        text = stringResource(MR.strings.shared_content),
        description = stringResource(MR.strings.shared_content_notification_desc),
        onCheckedChange = {}
    )
}