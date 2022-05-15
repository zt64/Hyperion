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
import androidx.hilt.navigation.compose.hiltViewModel
import com.hyperion.R
import com.hyperion.preferences.Prefs
import com.hyperion.preferences.VideoCardStyle
import com.hyperion.ui.components.NavigationDestination
import com.hyperion.ui.components.settings.SettingItem
import com.hyperion.ui.components.settings.ThemePicker
import com.hyperion.ui.viewmodel.SettingsViewModel
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
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
            modifier = Modifier.clickable { Prefs.materialYou = !Prefs.materialYou },
            text = { Text("Material You") },
            trailing = {
                Switch(checked = Prefs.materialYou, onCheckedChange = { Prefs.materialYou = it })
            }
        )

        SettingItem(
            modifier = Modifier.clickable(enabled = !Prefs.materialYou) {
                Prefs.blackBackground = !Prefs.blackBackground
            },
            text = { Text(stringResource(R.string.black_background)) },
            trailing = {
                Switch(
                    enabled = !Prefs.materialYou,
                    checked = Prefs.blackBackground,
                    onCheckedChange = { Prefs.blackBackground = it }
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
                    Text(Prefs.theme.displayName)
                }
            }
        )

        var showVideoCardStyleDropdown by remember { mutableStateOf(false) }
        SettingItem(
            modifier = Modifier.clickable { showVideoCardStyleDropdown = true },
            icon = {
                Icon(
                    imageVector = Icons.Default.VideoSettings,
                    contentDescription = null
                )
            },
            text = { Text("Video card style") },
            trailing = {
                Box {
                    FilledTonalButton(
                        onClick = { showVideoCardStyleDropdown = true }
                    ) {
                        Text(Prefs.videoCardStyle.displayName)
                    }

                    DropdownMenu(
                        expanded = showVideoCardStyleDropdown,
                        onDismissRequest = { showVideoCardStyleDropdown = false }
                    ) {
                        VideoCardStyle.values().forEach { style ->
                            DropdownMenuItem(
                                text = { Text(style.displayName) },
                                onClick = {
                                    Prefs.videoCardStyle = style
                                    showVideoCardStyleDropdown = false
                                }
                            )
                        }
                    }
                }
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
                        Text(Prefs.startScreen.name)
                    }

                    DropdownMenu(
                        expanded = showStartScreenDropdown,
                        onDismissRequest = { showStartScreenDropdown = false }
                    ) {
                        NavigationDestination.values().forEach { destination ->
                            DropdownMenuItem(
                                text = { Text(destination.name) },
                                onClick = { Prefs.startScreen = destination }
                            )
                        }
                    }
                }
            }
        )

        val directoryChooser = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) Prefs.downloadDirectory = uri.toString()
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
            secondaryText = { Text(Prefs.downloadDirectory) }
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