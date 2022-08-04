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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.ui.theme.Theme
import com.hyperion.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel(),
    onClickBack: () -> Unit
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.settings_screen)) },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.NavigateBefore,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val prefs = viewModel.prefs

            if (viewModel.showThemePicker) {
                ThemePicker(
                    currentTheme = prefs.theme,
                    onDismissRequest = viewModel::dismissThemePicker,
                    onConfirm = viewModel::setTheme
                )
            }

            SwitchSetting(
                checked = prefs.dynamicColor,
                text = stringResource(R.string.dynamic_color),
                onCheckedChange = { prefs.dynamicColor = it }
            )

            SwitchSetting(
                enabled = !prefs.dynamicColor,
                checked = prefs.midnightMode,
                text = stringResource(R.string.black_background),
                onCheckedChange = { prefs.midnightMode = it }
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

            SwitchSetting(
                checked = prefs.compactCard,
                text = stringResource(R.string.compact_card),
                icon = Icons.Default.VideoSettings,
                onCheckedChange = { prefs.compactCard = it }
            )

            Divider()

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

            SwitchSetting(
                enabled = false,
                checked = prefs.pictureInPicture,
                text = stringResource(R.string.pip),
                icon = Icons.Default.PictureInPicture,
                onCheckedChange = { prefs.pictureInPicture = it }
            )

            SliderSetting(
                text = stringResource(R.string.timestamp_scale),
                suffix = "x",
                value = prefs.timestampScale,
                valueRange = 0.8f..2f,
                onValueChangeFinished = { prefs.timestampScale = it }
            )

            Divider()

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
    }
}

@Composable
fun ThemePicker(
    currentTheme: Theme,
    onDismissRequest: () -> Unit,
    onConfirm: (Theme) -> Unit,
) {
    var selectedTheme by remember { mutableStateOf(currentTheme) }

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
                            text = theme.displayName,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderSetting(
    text: String,
    suffix: String = "",
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 10,
    onValueChange: (value: Float) -> Unit = { },
    onValueChangeFinished: (value: Float) -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    ListItem(
        headlineText = { Text(text) },
        supportingText = {
            var sliderValue by remember { mutableStateOf(value) }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    modifier = Modifier.weight(0.5f),
                    value = sliderValue,
                    valueRange = valueRange,
                    steps = steps,
                    onValueChange = {
                        onValueChange(it)
                        sliderValue = it
                    },
                    onValueChangeFinished = { onValueChangeFinished(sliderValue) }
                )

                Text("${"%.1f".format(sliderValue)}$suffix")
            }
        },
        trailingContent = trailingContent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchSetting(
    enabled: Boolean = true,
    checked: Boolean,
    icon: ImageVector? = null,
    text: String,
    supportingText: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(enabled) {
            onCheckedChange(!checked)
        },
        headlineText = { Text(text) },
        supportingText = supportingText?.let { { Text(it) } },
        leadingContent = icon?.let { imageVector ->
            { Icon(imageVector = imageVector, contentDescription = text) }
        },
        trailingContent = {
            Switch(
                enabled = enabled,
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}