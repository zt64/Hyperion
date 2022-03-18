package com.hyperion.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.api.ApiException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import com.hyperion.preferences.Prefs
import com.hyperion.ui.components.ListItem
import com.hyperion.ui.components.settings.ThemeDialog
import com.hyperion.ui.components.settings.ThemeSetting
import com.hyperion.util.AuthResultContract
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Destination
@Composable
fun SettingsScreen() {
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
            onClick = {
                authResultLauncher.launch(signInRequestCode)
            }
        ) {
            Text("Sign-in")
        }

        var showThemeDialog by remember { mutableStateOf(false) }

        if (showThemeDialog) {
            ThemeDialog(
                onDismissRequest = { showThemeDialog = false },
                onConfirm = {
                    Prefs.theme = it
                }
            )
        }

        val directoryChooser = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) Prefs.downloadDirectory = uri.toString()
        }

        ThemeSetting(onClick = { showThemeDialog = true })
        ListItem(
            modifier = Modifier.clickable { directoryChooser.launch(null) },
            text = { Text("Download location") },
            secondaryText = { Text(Prefs.downloadDirectory) }
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