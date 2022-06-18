package com.hyperion.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.components.NavigationDestination
import com.hyperion.ui.theme.Theme
import com.hyperion.ui.viewmodel.SettingsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.get
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
            text = { Text(stringResource(R.string.compact_card)) },
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

        SettingItem(
            modifier = Modifier.clickable(
                enabled = false // TODO: Remove when PIP is supported
            ) { prefs.pictureInPicture = !prefs.pictureInPicture },
            icon = {
                Icon(
                    imageVector = Icons.Default.PictureInPicture,
                    contentDescription = stringResource(R.string.pip)
                )
            },
            text = { Text(stringResource(R.string.pip)) },
            trailing = {
                Switch(
                    checked = prefs.pictureInPicture,
                    onCheckedChange = { prefs.pictureInPicture = !prefs.pictureInPicture },
                    enabled = false // TODO: Remove when PIP is supported
                )
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
            secondaryText = { Text(prefs.downloadDirectory ?: stringResource(R.string.unknown)) }
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

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit) = { },
    text: @Composable () -> Unit,
    secondaryText: @Composable (() -> Unit) = { },
    trailing: @Composable (() -> Unit) = { },
) {
    Row(
        modifier = modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            ProvideTextStyle(
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                )
            ) {
                text()
            }
            ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                secondaryText()
            }
        }

        Spacer(Modifier.weight(1f, true))

        trailing()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePicker(
    onDismissRequest: () -> Unit,
    onConfirm: (Theme) -> Unit,
    prefs: PreferencesManager = get()
) {
    var selectedTheme by remember { mutableStateOf(prefs.theme) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.theme)) },
        text = {
            Column {
                Theme.values().forEach { theme ->
                    Row(
                        modifier = Modifier.clickable { selectedTheme = theme },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            theme.displayName,
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(Modifier.weight(1f, true))

                        RadioButton(
                            selected = theme == selectedTheme,
                            onClick = { selectedTheme = theme }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedTheme)
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.apply))
            }
        }
    )
}