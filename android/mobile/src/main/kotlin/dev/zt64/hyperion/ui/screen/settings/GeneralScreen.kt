package dev.zt64.hyperion.ui.screen.settings

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.ui.component.setting.RadioSetting
import dev.zt64.hyperion.ui.component.setting.SwitchSetting

context(ColumnScope)
@Composable
fun GeneralScreen(
    preferences: PreferencesManager,
    onClickDownload: (uri: Uri?) -> Unit,
) {
    RadioSetting(
        preference = preferences::startScreen,
        icon = Icons.Default.Start,
        label = stringResource(MR.strings.start_screen)
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    val directoryChooser = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = onClickDownload
    )

    ListItem(
        modifier = Modifier.clickable { directoryChooser.launch(null) },
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