package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.BuildKonfig
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.model.SettingsScreenModel

class AboutScreen : Screen {
    @Composable
    override fun Content() {
        val model: SettingsScreenModel = koinScreenModel()

        ListItem(
            modifier = Modifier.clickable(onClick = model::checkForUpdates),
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Update,
                    contentDescription = stringResource(MR.strings.version)
                )
            },
            trailingContent = {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = CircleShape
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = "v${BuildKonfig.VERSION_NAME}",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            headlineContent = { Text(stringResource(MR.strings.version)) },
            supportingContent = { Text(stringResource(MR.strings.check_for_updates)) }
        )

        ListItem(
            modifier = Modifier.clickable(onClick = model::openGitHub),
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = stringResource(MR.strings.github)
                )
            },
            headlineContent = { Text(stringResource(MR.strings.github)) }
        )
    }
}