package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.model.SettingsScreenModel

object AccountsScreen : Screen {
    @Composable
    override fun Content() {
        val model: SettingsScreenModel = koinScreenModel()
        val preferences = model.preferences

        var showAccountDialog by rememberSaveable { mutableStateOf(false) }

        // val preferences: PreferencesManager = koinInject()
        // val accountManager: AccountManager = koinInject()
        //
        // if (accountManager.loggedIn) {
        //
        // }

        if (showAccountDialog) {
            AddAccountDialog(
                onDismissRequest = { showAccountDialog = false }
            )
        }

        // LazyColumn(
        //     modifier = Modifier.weight(1f, true)
        // ) {
        //
        // }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = {
                showAccountDialog = true
            }
        ) {
            Text(stringResource(MR.strings.add_account))
        }
    }
}

@Composable
private fun AccountCard() {
    Card {
    }
}

@Composable
private fun AddAccountDialog(onDismissRequest: () -> Unit) {
    var showLoading by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                enabled = true,
                onClick = { showLoading = true },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                AnimatedVisibility(visible = showLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                }

                Text("Add")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(MR.strings.dismiss))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.ManageAccounts,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(MR.strings.add_account))
        },
        text = {
            Column {
                TextField(
                    value = "AEYM-PQOE",
                    onValueChange = {},
                    readOnly = true
                )
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}