package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.SUPPORTS_PIP
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.setting.SwitchSetting
import dev.zt64.hyperion.ui.model.SettingsScreenModel
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher

class GeneralScreen : Screen {
    @Composable
    override fun Content() {
        val model: SettingsScreenModel = koinScreenModel()
        val preferences = model.preferences

        if (SUPPORTS_PIP) {
            SwitchSetting(
                preference = preferences::pictureInPicture,
                enabled = false,
                text = stringResource(MR.strings.pip),
                icon = Icons.Default.PictureInPicture
            )
        }

        SwitchSetting(
            preference = preferences::miniPlayer,
            text = stringResource(MR.strings.mini_player),
            icon = Icons.Default.Minimize
        )

        var showDirectoryPicker by rememberSaveable { mutableStateOf(false) }

        val pickerLauncher = rememberDirectoryPickerLauncher {
            model.setDownloadUri(it?.path)
        }

        ListItem(
            modifier = Modifier.clickable(onClick = pickerLauncher::launch),
            headlineContent = { Text(stringResource(MR.strings.download_location)) },
            supportingContent = { Text(preferences.downloadDirectory) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(MR.strings.download_location)
                )
            }
        )
    }
}