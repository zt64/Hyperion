package dev.zt64.hyperion.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.domain.model.SkipOption
import dev.zt64.hyperion.domain.model.SponsorBlockCategory
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.setting.SliderSetting
import dev.zt64.hyperion.ui.component.setting.SwitchSetting
import dev.zt64.hyperion.ui.form.Form
import dev.zt64.hyperion.ui.model.SettingsScreenModel

class SponsorBlockScreen : Screen {
    @Composable
    override fun Content() {
        val model: SettingsScreenModel = koinScreenModel()
        val preferences = model.preferences

        SwitchSetting(
            preference = preferences::sponsorBlockEnabled,
            text = stringResource(MR.strings.enabled)
        )

        AnimatedVisibility(
            visible = preferences.sponsorBlockEnabled,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var showUserIdDialog by rememberSaveable { mutableStateOf(false) }

                SliderSetting(
                    text = stringResource(MR.strings.skip_notice_duration),
                    value = preferences.sponsorBlockSkipNoticeDuration.toFloat(),
                    valueRange = 0f..10f,
                    onValueChangeFinished = {
                        preferences.sponsorBlockSkipNoticeDuration = it.toInt()
                    }
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
                    text = stringResource(MR.strings.skip_count_tracking)
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

                val isErrorUrl by remember { mutableStateOf(false) }
                var sbUrl by remember(preferences.sponsorBlockApiUrl) {
                    mutableStateOf(preferences.sponsorBlockApiUrl)
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = sbUrl,
                    onValueChange = {
                        sbUrl = it

                        if (it.isNotEmpty()) {
                            preferences.sponsorBlockApiUrl = it
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    label = {
                        Text("API URL")
                    },
                    leadingIcon = {
                        if (preferences.sponsorBlockApiUrl.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    preferences.sponsorBlockApiUrl = "https://sponsor.ajay.app"
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    isError = isErrorUrl
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                SponsorBlockCategory.entries.forEach { category ->
                    var showDialog by rememberSaveable { mutableStateOf(false) }

                    if (showDialog) {
                        Form(
                            onDismissRequest = {
                                showDialog = false
                            },
                            title = {
                                Text(category.name)
                            }
                        ) {
                            Column {
                                SkipOption.entries.forEach { skipOption ->
                                    ListItem(
                                        modifier = Modifier.clickable { showDialog = false },
                                        headlineContent = {
                                            Text(stringResource(skipOption.label))
                                        }
                                    )
                                }
                            }
                        }
                    }

                    SwitchSetting(
                        modifier = Modifier.clickable { showDialog = true },
                        checked = false,
                        onCheckedChange = { },
                        text = stringResource(category.label),
                        description = category.description
                        // trailingContent = {
                        //     Box(
                        //         modifier = Modifier
                        //             .size(36.dp)
                        //             .background(category.defaultColor, CircleShape)
                        //     )
                        // }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserIdDialog(
    id: String?,
    onClickSave: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var text by rememberSaveable { mutableStateOf(id.orEmpty()) }

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