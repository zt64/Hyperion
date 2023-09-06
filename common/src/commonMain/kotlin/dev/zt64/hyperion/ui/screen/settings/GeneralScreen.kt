package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.SUPPORTS_PIP
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.ui.component.setting.RadioSetting
import dev.zt64.hyperion.ui.component.setting.SwitchSetting

@Composable
fun GeneralScreen(
    preferences: PreferencesManager,
    onClickDownload: (path: String?) -> Unit,
) {
    RadioSetting(
        preference = preferences::startScreen,
        icon = Icons.Default.Start,
        label = stringResource(MR.strings.start_screen)
    )

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

    DirectoryPicker(showDirectoryPicker) {
        showDirectoryPicker = false
        onClickDownload(it)
    }

    ListItem(
        modifier = Modifier.clickable { showDirectoryPicker = true },
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