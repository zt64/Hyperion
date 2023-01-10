package com.hyperion.ui.component.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import kotlinx.collections.immutable.ImmutableMap

@Composable
fun <T> RadioSetting(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
    description: String? = null,
    icon: ImageVector? = null,
    value: T,
    options: ImmutableMap<String, T>,
    onConfirm: (value: T) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    if (showDialog) {
        var selectedOption by rememberSaveable { mutableStateOf(value) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = icon?.let {
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            },
            title = {
                Text(label)
            },
            text = {
                Column {
                    options.forEach { (label, option) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedOption = option },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOption == option,
                                onClick = { selectedOption = option }
                            )

                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                softWrap = false
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(selectedOption)
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.dismiss))
                }
            }
        )
    }

    ListItem(
        modifier = modifier.clickable {
            showDialog = true
        },
        headlineText = {
            Text(label)
        },
        supportingText = description?.let {
            {
                Text(description)
            }
        },
        leadingContent = icon?.let {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        },
        trailingContent = {
            Button(
                enabled = enabled,
                onClick = { showDialog = true }
            ) {
                Text(options.filterValues { it == value }.keys.single())
            }
        }
    )
}