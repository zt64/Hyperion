package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.BuildConfig
import dev.zt64.hyperion.MR

context(ColumnScope)
@Composable
fun AboutScreen(
    onClickUpdate: () -> Unit,
    onClickGithub: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClickUpdate),
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
                    text = "v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        headlineContent = { Text(stringResource(MR.strings.version)) },
        supportingContent = { Text(stringResource(MR.strings.check_for_updates)) }
    )

    ListItem(
        modifier = Modifier.clickable(onClick = onClickGithub),
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = stringResource(MR.strings.github)
            )
        },
        headlineContent = { Text(stringResource(MR.strings.github)) }
    )
}