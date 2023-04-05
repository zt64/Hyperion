package com.hyperion.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.component.setting.SliderSetting
import com.hyperion.ui.component.setting.SwitchSetting

context(ColumnScope)
@Composable
fun SponsorBlockScreen(
    preferences: PreferencesManager
) {
    SwitchSetting(
        checked = preferences.sponsorBlockEnabled,
        text = stringResource(R.string.enabled),
        onCheckedChange = { preferences.sponsorBlockEnabled = it }
    )

    AnimatedVisibility(visible = preferences.sponsorBlockEnabled) {
        var showUserIdDialog by rememberSaveable { mutableStateOf(false) }

        SliderSetting(
            text = stringResource(R.string.skip_notice_duration),
            value = preferences.sponsorBlockSkipNoticeDuration.toFloat(),
            valueRange = 0f..10f,
            onValueChange = { preferences.sponsorBlockSkipNoticeDuration = it.toInt() },
            onValueChangeFinished = { preferences.sponsorBlockSkipNoticeDuration = it.toInt() }
        )

        if (showUserIdDialog) {
            UserIdDialog(
                id = preferences.sponsorBlockUserIdPrivate,
                onClickSave = { /*TODO*/ },
                onDismissRequest = { showUserIdDialog = false }
            )
        }

        ListItem(
            modifier = Modifier.clickable { showUserIdDialog = true },
            headlineContent = {
                Text("Private UserID")
            },
            supportingContent = {
                Text("**********************")
            },
            trailingContent = {
                IconButton(onClick = { showUserIdDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            }
        )
    }
}

@Composable
private fun UserIdDialog(
    id: String,
    onClickSave: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var text by rememberSaveable { mutableStateOf(id) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onClickSave) {
                Text("Save")
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.PermIdentity,
                contentDescription = null
            )
        },
        title = {
            Text("Private UserID")
        },
        text = {
            val focusRequester = FocusRequester()

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = text,
                onValueChange = { text = it },
                trailingIcon = {
                    val clipboard = LocalClipboardManager.current

                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(text))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    )
}