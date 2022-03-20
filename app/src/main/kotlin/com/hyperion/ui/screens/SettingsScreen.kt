package com.hyperion.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.hyperion.ui.components.ListItem
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

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                val account = task?.getResult(ApiException::class.java)

                if (account == null) {
                    Toast.makeText(context, "Google sign in failed", Toast.LENGTH_LONG).show()
                } else {
                    coroutineScope.launch(Dispatchers.IO) {
                        val transport = NetHttpTransport()
                        val jsonFactory = GsonFactory()

                        val credential =
                            GoogleAccountCredential.usingOAuth2(context, listOf(YouTubeScopes.YOUTUBE_READONLY))
                                .setBackOff(ExponentialBackOff())
                                .setSelectedAccountName(account.account!!.name)

                        val youtubeService = YouTube.Builder(transport, jsonFactory, credential)
                            .setApplicationName("Hyperion")
                            .build()
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google sign in failed", Toast.LENGTH_LONG).show()
            }
        }

    Column(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { authResultLauncher.launch(signInRequestCode) }
        ) {
            Text("Sign-in to Google")
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
                    Text(Prefs.theme.displayName)
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
                        DropdownMenuItem(text = { Text("Large") }, onClick = { /*TODO*/ })
                        DropdownMenuItem(text = { Text("Compact") }, onClick = { /*TODO*/ })
                    }
                }
            }
        )

        ListItem(
            modifier = Modifier.clickable { directoryChooser.launch(null) },
            icon = { Icon(imageVector = Icons.Default.Download, contentDescription = "Download Setting") },
            text = { Text(stringResource(R.string.download_location)) },
            secondaryText = { Text(Prefs.downloadDirectory) }
        )

        ListItem(
            modifier = Modifier.clickable {  },
            icon = { Icon(imageVector = Icons.Default.Api, contentDescription = "Invidious API Instance Setting") },
            text = { Text("Invidious instance") },
            secondaryText = { Text("Some url") }
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