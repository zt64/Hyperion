package com.hyperion.ui.screen

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
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.theme.Theme
import com.hyperion.ui.viewmodel.SettingsViewModel
import com.xinto.taxi.BackstackNavigator
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel(),
    navigator: BackstackNavigator<AppDestination>
) {
    val prefs = viewModel.prefs
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarScrollState())

    Scaffold(
        modifier = Modifier,
        topBar = {
            MediumTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.Default.NavigateBefore,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues),
//                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (viewModel.showThemePicker) {
                ThemePicker(
                    onDismissRequest = viewModel::dismissThemePicker,
                    onConfirm = viewModel::setTheme
                )
            }

            ListItem(
                modifier = Modifier.clickable { prefs.dynamicColor = !prefs.dynamicColor },
                headlineText = { Text(stringResource(R.string.dynamic_color)) },
                trailingContent = {
                    Switch(checked = prefs.dynamicColor, onCheckedChange = { prefs.dynamicColor = it })
                }
            )

            ListItem(
                modifier = Modifier.clickable(enabled = !prefs.dynamicColor) {
                    prefs.midnightMode = !prefs.midnightMode
                },
                headlineText = { Text(stringResource(R.string.black_background)) },
                trailingContent = {
                    Switch(
                        enabled = !prefs.dynamicColor,
                        checked = prefs.midnightMode,
                        onCheckedChange = { prefs.midnightMode = it }
                    )
                }
            )

            ListItem(
                modifier = Modifier.clickable { viewModel.showThemePicker() },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = null
                    )
                },
                headlineText = { Text(stringResource(R.string.theme)) },
                supportingText = { Text(stringResource(R.string.theme_setting_description)) },
                trailingContent = {
                    FilledTonalButton(
                        onClick = viewModel::showThemePicker
                    ) {
                        Text(prefs.theme.displayName)
                    }
                }
            )


            ListItem(
                modifier = Modifier.clickable { prefs.compactCard = !prefs.compactCard },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.VideoSettings,
                        contentDescription = null
                    )
                },
                headlineText = { Text(stringResource(R.string.compact_card)) },
                trailingContent = {
                    Switch(
                        checked = prefs.compactCard,
                        onCheckedChange = { prefs.compactCard = it }
                    )
                }
            )

            var showStartScreenDropdown by remember { mutableStateOf(false) }
            ListItem(
                modifier = Modifier.clickable { showStartScreenDropdown = true },
                leadingContent = { Icon(imageVector = Icons.Default.Start, contentDescription = null) },
                headlineText = { Text(stringResource(R.string.start_screen)) },
                trailingContent = {
                    Box {
                        FilledTonalButton(
                            onClick = { showStartScreenDropdown = true }
                        ) {
                            Text(stringResource(prefs.startScreen.label))
                        }

                        DropdownMenu(
                            expanded = showStartScreenDropdown,
                            onDismissRequest = { showStartScreenDropdown = false }
                        ) {
//                            NavigationDestination.values().forEach { destination ->
//                                DropdownMenuItem(
//                                    text = { Text(destination.name) },
//                                    onClick = { prefs.startScreen = destination }
//                                )
//                            }
                        }
                    }
                }
            )

            ListItem(
                modifier = Modifier.clickable(
                    enabled = false // TODO: Remove when PIP is supported
                ) { prefs.pictureInPicture = !prefs.pictureInPicture },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.PictureInPicture,
                        contentDescription = stringResource(R.string.pip)
                    )
                },
                headlineText = { Text(stringResource(R.string.pip)) },
                trailingContent = {
                    Switch(
                        checked = prefs.pictureInPicture,
                        onCheckedChange = { prefs.pictureInPicture = !prefs.pictureInPicture },
                        enabled = false // TODO: Remove when PIP is supported
                    )
                }
            )

            ListItem(
                headlineText = { Text("Timestamp scale") },
                supportingText = {
                    var timeStampScale by remember { mutableStateOf(prefs.timestampScale) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            modifier = Modifier.weight(0.75f, true),
                            value = timeStampScale,
                            valueRange = 0.8f..2f,
                            steps = 10,
                            onValueChange = { timeStampScale = it },
                            onValueChangeFinished = { prefs.timestampScale = timeStampScale }
                        )

                        Text(
                            text = "${"%.1f".format(timeStampScale)}x",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            )

            val directoryChooser = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
                if (uri != null) prefs.downloadDirectory = uri.toString()
            }
            ListItem(
                modifier = Modifier.clickable { directoryChooser.launch(null) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = stringResource(R.string.download_location)
                    )
                },
                headlineText = { Text(stringResource(R.string.download_location)) },
                supportingText = { Text(prefs.downloadDirectory ?: stringResource(R.string.unknown)) }
            )

            Divider()

            ListItem(
                modifier = Modifier.clickable { viewModel.openGitHub() },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = stringResource(R.string.github)
                    )
                },
                headlineText = { Text(stringResource(R.string.github)) }
            )
        }
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