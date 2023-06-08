package com.hyperion.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.component.player.SponsorBlockCategory
import com.hyperion.ui.component.setting.SliderSetting
import com.hyperion.ui.component.setting.SwitchSetting

context(ColumnScope)
@Composable
fun SponsorBlockScreen(
    preferences: PreferencesManager,
    onClickCategory: (SponsorBlockCategory) -> Unit
) {
    SwitchSetting(
        preference = preferences::sponsorBlockEnabled,
        text = stringResource(R.string.enabled),
    )

    AnimatedVisibility(visible = preferences.sponsorBlockEnabled) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var showUserIdDialog by rememberSaveable { mutableStateOf(false) }

            SliderSetting(
                text = stringResource(R.string.skip_notice_duration),
                value = preferences.sponsorBlockSkipNoticeDuration.toFloat(),
                valueRange = 0f..10f,
                onValueChangeFinished = { preferences.sponsorBlockSkipNoticeDuration = it.toInt() }
            )

            if (showUserIdDialog) {
                UserIdDialog(
                    id = preferences.sponsorBlockUserIdPrivate,
                    onClickSave = { showUserIdDialog = false },
                    onDismissRequest = { showUserIdDialog = false }
                )
            }

            SwitchSetting(
                preference = preferences::sponsorBlockSkipTracking,
                text = stringResource(R.string.skip_count_tracking)
            )

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

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = preferences.sponsorBlockApiUrl,
                onValueChange = { preferences.sponsorBlockApiUrl = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    imeAction = ImeAction.Done
                ),
                label = {
                    Text("API URL")
                }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            val categories = remember(SponsorBlockCategory::values)

            categories.forEach { category ->
                ListItem(
                    modifier = Modifier.clickable { onClickCategory(category) },
                    headlineContent = { Text(category.toString()) },
                    supportingContent = { Text(category.description) },
                    trailingContent = {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(category.color, CircleShape)
                        )
                    }
                )
            }
        }
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