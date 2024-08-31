package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.setting.SwitchSetting
import dev.zt64.hyperion.ui.model.SettingsScreenModel

class NotificationsScreen : Screen {
    @Composable
    override fun Content() {
        val model: SettingsScreenModel = koinScreenModel()
        val preferences = model.preferences

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
}