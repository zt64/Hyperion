package com.hyperion.ui.component.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import kotlin.reflect.KMutableProperty0

interface RadioOption {
    @get:StringRes
    val label: Int
}

@Composable
inline fun <reified E> RadioSetting(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    preference: KMutableProperty0<E>,
    description: String? = null,
    icon: ImageVector? = null,
    label: String,
) where E : Enum<E>, E : RadioOption {
    val options = remember { enumValues<E>() }

    RadioSetting(
        modifier = modifier,
        enabled = enabled,
        value = preference.get(),
        options = options,
        onConfirm = preference::set,
        label = label,
        description = description,
        icon = icon
    )
}

@Composable
fun <E : RadioOption> RadioSetting(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    value: E,
    options: Array<E>,
    onConfirm: (value: E) -> Unit,
    label: String,
    description: String? = null,
    icon: ImageVector? = null,
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
            title = { Text(label) },
            text = {
                Column {
                    options.forEach { option ->
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
                                text = stringResource(option.label),
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
                    onClick = { showDialog = false }
                ) {
                    Text(stringResource(R.string.dismiss))
                }
            }
        )
    }

    ListItem(
        modifier = modifier.clickable { showDialog = true },
        headlineContent = { Text(label) },
        supportingContent = description?.let {
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
                Text(stringResource(value.label))
            }
        }
    )
}