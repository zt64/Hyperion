package com.hyperion.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.ui.viewmodel.SettingsViewModel

context(ColumnScope)
@Composable
fun AboutScreen(
    viewModel: SettingsViewModel
) {
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