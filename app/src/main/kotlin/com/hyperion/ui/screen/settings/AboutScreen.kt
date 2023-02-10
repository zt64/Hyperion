package com.hyperion.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.BuildConfig
import com.hyperion.R
import com.hyperion.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

context(ColumnScope)
@Composable
fun AboutScreen(
    viewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    ListItem(
        modifier = Modifier.clickable {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Updating not yet implemented!")
            }
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Update,
                contentDescription = stringResource(R.string.version)
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
        headlineText = { Text(stringResource(R.string.version)) },
        supportingText = { Text(stringResource(R.string.check_for_updates)) }
    )

    ListItem(
        modifier = Modifier.clickable(onClick = viewModel::openGitHub),
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = stringResource(R.string.github)
            )
        },
        headlineText = { Text(stringResource(R.string.github)) }
    )
}