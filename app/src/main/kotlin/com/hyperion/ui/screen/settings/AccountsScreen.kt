package com.hyperion.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.hyperion.R
import com.hyperion.domain.manager.AccountManager
import com.hyperion.domain.manager.PreferencesManager
import org.koin.compose.koinInject

context(ColumnScope)
@Composable
fun AccountsScreen(
    onClickAddAccount: () -> Unit,
    preferences: PreferencesManager = koinInject(),
    accountManager: AccountManager = koinInject()
) {
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

    LazyColumn(
        modifier = Modifier
            .weight(1f, true)
    ) {

    }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = {
            showAccountDialog = true
        }
    ) {
        Text(stringResource(R.string.add_account))
    }
}

@Composable
private fun AccountCard() {
    Card {

    }
}

@Composable
private fun AddAccountDialog(
    onDismissRequest: () -> Unit
) {
    var showLoading by remember { mutableStateOf(false) }

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
                Text(stringResource(R.string.dismiss))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.ManageAccounts,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.add_account))
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