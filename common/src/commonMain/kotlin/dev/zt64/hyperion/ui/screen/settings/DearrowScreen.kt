package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.zt64.hyperion.ui.component.setting.SwitchSetting
import dev.zt64.hyperion.ui.model.SettingsScreenModel

object DearrowScreen : Screen {
    @Composable
    override fun Content() {
        val model: SettingsScreenModel = koinScreenModel()
        val preferences = model.preferences

        SwitchSetting(
            preference = preferences::dearrowEnabled,
            text = "Enable Dearrow"
        )

        SwitchSetting(
            enabled = preferences.dearrowEnabled,
            preference = preferences::dearrowReplaceTitle,
            text = "Replace title"
        )

        SwitchSetting(
            enabled = preferences.dearrowEnabled,
            preference = preferences::dearrowReplaceThumbnail,
            text = "Replace thumbnail"
        )
    }
}