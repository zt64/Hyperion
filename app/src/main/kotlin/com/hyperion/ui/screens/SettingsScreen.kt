package com.hyperion.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.common.api.ApiException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import com.hyperion.R
import com.hyperion.preferences.Prefs
import com.hyperion.preferences.VideoCardStyle
import com.hyperion.ui.components.ListItem
import com.hyperion.ui.components.NavigationDestination
import com.hyperion.ui.components.settings.ThemePicker
import com.hyperion.ui.viewmodel.SettingsViewModel
import com.hyperion.util.AuthResultContract
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Destination
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val signInRequestCode = 1

    val signInFailed = stringResource(R.string.login_failed)
    val appName = stringResource(R.string.app_name)

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                val account = task?.getResult(ApiException::class.java)

                if (account == null)
                    Toast.makeText(context, signInFailed, Toast.LENGTH_LONG).show()
                else {
                    coroutineScope.launch(Dispatchers.IO) {
                        val transport = NetHttpTransport()
                        val jsonFactory = GsonFactory()

                        val credential =
                            GoogleAccountCredential.usingOAuth2(context, listOf(YouTubeScopes.YOUTUBE_READONLY))
                                .setBackOff(ExponentialBackOff())
                                .setSelectedAccountName(account.account!!.name)

                        val youtubeService = YouTube.Builder(transport, jsonFactory, credential)
                            .setApplicationName(appName)
                            .build()
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(context, signInFailed, Toast.LENGTH_LONG).show()
            }
        }

    Column(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { authResultLauncher.launch(signInRequestCode) }
        ) {
            Text(stringResource(R.string.login_google))
        }

        if (viewModel.showThemePicker) {
            ThemePicker(
                onDismissRequest = viewModel::dismissThemePicker,
                onConfirm = viewModel::setTheme
            )
        }

        val directoryChooser = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) Prefs.downloadDirectory = uri.toString()
        }

        ListItem(
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
                    Text(Prefs.theme.toDisplayName())
                }
            }
        )

        var showVideoCardStyleDropdown by remember { mutableStateOf(false) }
        ListItem(
            modifier = Modifier.clickable { showVideoCardStyleDropdown = true },
            icon = {
                Icon(
                    imageVector = Icons.Default.VideoSettings,
                    contentDescription = null
                )
            },
            text = { Text(stringResource(R.string.style_video_card)) },
            trailing = {
                Box {
                    FilledTonalButton(
                        onClick = { showVideoCardStyleDropdown = true }
                    ) {
                        Text(Prefs.videoCardStyle.toDisplayName())
                    }

                    DropdownMenu(
                        expanded = showVideoCardStyleDropdown,
                        onDismissRequest = { showVideoCardStyleDropdown = false }
                    ) {
                        VideoCardStyle.values().forEach {
                            DropdownMenuItem(
                                text = { Text(it.toDisplayName()) },
                                onClick = { /*TODO*/ }
                            )
                        }
                    }
                }
            }
        )

        var showStartScreenDropdown by remember { mutableStateOf(false) }
        ListItem(
            modifier = Modifier.clickable { showStartScreenDropdown = true },
            icon = {
                Icon(
                    imageVector = Icons.Default.Start,
                    contentDescription = stringResource(R.string.desc_start_screen_setting)
                )
            },
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

        ListItem(
            modifier = Modifier.clickable { directoryChooser.launch(null) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(R.string.desc_download_setting)
                )
            },
            text = { Text(stringResource(R.string.download_location)) },
            secondaryText = { Text(Prefs.downloadDirectory) }
        )

        ListItem(
            modifier = Modifier.clickable { },
            icon = {
                Icon(
                    imageVector = Icons.Default.Api,
                    contentDescription = stringResource(R.string.desc_invidious_setting)
                )
            },
            text = { Text(stringResource(R.string.invidious_instance)) },
            secondaryText = { Text(stringResource(R.string.url)) }
        )

        Divider()

        ListItem(
            modifier = Modifier.clickable { viewModel.openGitHub() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = stringResource(R.string.github)
                )
            },
            text = { Text(stringResource(R.string.github)) }
        )

//        var showRegionDialog by remember { mutableStateOf(false) }

//        if (showRegionDialog) {
//            RegionDialog(
//                onDismissRequest = { showRegionDialog = false },
//                onConfirm = { /* TODO: Save region */ }
//            )
//        }
//
//        ListItem(
//            text = { Text("Region") },
//            trailing = {
//                FilledTonalButton(
//                    onClick = { showRegionDialog = true }
//                ) {
//                    Text("US")
//                }
//            }
//        )
    }
}