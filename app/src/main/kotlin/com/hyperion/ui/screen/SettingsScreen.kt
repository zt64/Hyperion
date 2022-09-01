package com.hyperion.ui.screen

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.ui.navigation.HomeDestination
import com.hyperion.ui.theme.Theme
import com.hyperion.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel(),
    onClickBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 4.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val preferences = viewModel.preferences

            if (viewModel.showThemePicker) {
                ThemePicker(
                    currentTheme = preferences.theme,
                    onDismissRequest = viewModel::dismissThemePicker,
                    onConfirm = viewModel::setTheme
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SwitchSetting(
                    checked = preferences.dynamicColor,
                    text = stringResource(R.string.dynamic_color),
                    icon = Icons.Default.Palette,
                    onCheckedChange = { preferences.dynamicColor = it }
                )
            }

            ListItem(
                modifier = Modifier.clickable(onClick = viewModel::showThemePicker),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = null
                    )
                },
                headlineText = { Text(stringResource(R.string.theme)) },
                supportingText = { Text(stringResource(R.string.theme_setting_description)) },
                trailingContent = {
                    FilledTonalButton(onClick = viewModel::showThemePicker) {
                        Text(preferences.theme.displayName)
                    }
                }
            )

            SwitchSetting(
                checked = preferences.compactCard,
                text = stringResource(R.string.compact_card),
                icon = Icons.Default.VideoSettings,
                onCheckedChange = { preferences.compactCard = it }
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
                            Text(stringResource(preferences.startScreen.label))
                        }

                        DropdownMenu(
                            expanded = showStartScreenDropdown,
                            onDismissRequest = { showStartScreenDropdown = false }
                        ) {
                            HomeDestination.values().forEach { destination ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(destination.label)) },
                                    onClick = {
                                        preferences.startScreen = destination
                                        showStartScreenDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            )

            SwitchSetting(
                enabled = false,
                checked = preferences.pictureInPicture,
                text = stringResource(R.string.pip),
                icon = Icons.Default.PictureInPicture,
                onCheckedChange = { preferences.pictureInPicture = it }
            )

            SliderSetting(
                text = stringResource(R.string.timestamp_scale),
                value = preferences.timestampScale,
                valueRange = 0.8f..2f,
                onValueChangeFinished = { preferences.timestampScale = it }
            )

            SwitchSetting(
                checked = preferences.showDownloadButton,
                text = stringResource(R.string.show_download_button),
                icon = Icons.Default.Download,
                onCheckedChange = { preferences.showDownloadButton = it }
            )

            AnimatedVisibility(visible = preferences.showDownloadButton) {
                val directoryChooser = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
                    if (uri != null) preferences.downloadDirectory = uri.toString()
                }

                ListItem(
                    modifier = Modifier.clickable { directoryChooser.launch(null) },
                    headlineText = { Text(stringResource(R.string.download_location)) },
                    supportingText = { Text(preferences.downloadDirectory) }
                )
            }

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
                            modifier = Modifier.weight(1f, true),
                            text = theme.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            softWrap = true
                        )

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

                Text("${"%.1f".format(sliderValue)}x")
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