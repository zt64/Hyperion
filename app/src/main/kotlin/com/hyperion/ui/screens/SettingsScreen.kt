package com.hyperion.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.ui.components.NavigationDestination
import com.hyperion.ui.components.settings.SettingItem
import com.hyperion.ui.components.settings.ThemePicker
import com.hyperion.ui.viewmodel.SettingsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel()
) {
    val prefs = viewModel.prefs

    Column(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (viewModel.showThemePicker) {
            ThemePicker(
                onDismissRequest = viewModel::dismissThemePicker,
                onConfirm = viewModel::setTheme
            )
        }

        SettingItem(
            modifier = Modifier.clickable { prefs.materialYou = !prefs.materialYou },
            text = { Text("Material You") },
            trailing = {
                Switch(checked = prefs.materialYou, onCheckedChange = { prefs.materialYou = it })
            }
        )

        SettingItem(
            modifier = Modifier.clickable(enabled = !prefs.materialYou) {
                prefs.midnightMode = !prefs.midnightMode
            },
            text = { Text(stringResource(R.string.black_background)) },
            trailing = {
                Switch(
                    enabled = !prefs.materialYou,
                    checked = prefs.midnightMode,
                    onCheckedChange = { prefs.midnightMode = it }
                )
            }
        )

        SettingItem(
            modifier = Modifier.clickable { viewModel.showThemePicker() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Style,
                    contentDescription = null
                )
            },
            text = { Text(stringResource(R.string.theme)) },
            secondaryText = { Text(stringResource(R.string.theme_setting_description)) },
            trailing = {
                FilledTonalButton(
                    onClick = viewModel::showThemePicker
                ) {
                    Text(prefs.theme.displayName)
                }
            }
        )


        SettingItem(
            modifier = Modifier.clickable { prefs.compactCard = !prefs.compactCard },
            icon = {
                Icon(
                    imageVector = Icons.Default.VideoSettings,
                    contentDescription = null
                )
            },
            text = { Text("Compact cards") },
            trailing = {
                Switch(
                    checked = prefs.compactCard,
                    onCheckedChange = { prefs.compactCard = it }
                )
            }
        )

        var showStartScreenDropdown by remember { mutableStateOf(false) }
        SettingItem(
            modifier = Modifier.clickable { showStartScreenDropdown = true },
            icon = { Icon(imageVector = Icons.Default.Start, contentDescription = null) },
            text = { Text(stringResource(R.string.start_screen)) },
            trailing = {
                Box {
                    FilledTonalButton(
                        onClick = { showStartScreenDropdown = true }
                    ) {
                        Text(prefs.startScreen.name)
                    }

                    DropdownMenu(
                        expanded = showStartScreenDropdown,
                        onDismissRequest = { showStartScreenDropdown = false }
                    ) {
                        NavigationDestination.values().forEach { destination ->
                            DropdownMenuItem(
                                text = { Text(destination.name) },
                                onClick = { prefs.startScreen = destination }
                            )
                        }
                    }
                }
            }
        )

        val directoryChooser = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) prefs.downloadDirectory = uri.toString()
        }
        SettingItem(
            modifier = Modifier.clickable { directoryChooser.launch(null) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(R.string.download_location)
                )
            },
            text = { Text(stringResource(R.string.download_location)) },
            secondaryText = { Text(prefs.downloadDirectory ?: "Unknown") }
        )

        Divider()

        SettingItem(
            modifier = Modifier.clickable { viewModel.openGitHub() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = stringResource(R.string.github)
                )
            },
            text = { Text(stringResource(R.string.github)) }
        )
    }
}