package dev.zt64.hyperion.ui.component.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.domain.model.StringLabel
import dev.zt64.hyperion.resources.MR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.enums.enumEntries
import kotlin.reflect.KMutableProperty0

@Composable
internal inline fun <reified E> RadioSetting(
    preference: KMutableProperty0<E>,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    description: String? = null,
    icon: ImageVector? = null
) where E : Enum<E>, E : StringLabel {
    val options = remember { enumEntries<E>() }

    RadioSetting(
        modifier = modifier,
        enabled = enabled,
        value = preference.get(),
        options = options.toImmutableList(),
        onConfirm = preference::set,
        label = label,
        description = description,
        icon = icon
    )
}

@Composable
internal fun <E : StringLabel> RadioSetting(
    label: String,
    value: E,
    options: ImmutableList<E>,
    onConfirm: (value: E) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    description: String? = null,
    icon: ImageVector? = null
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
                OutlinedButton(
                    onClick = {
                        onConfirm(selectedOption)
                        showDialog = false
                    }
                ) {
                    Text(stringResource(MR.strings.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(stringResource(MR.strings.dismiss))
                }
            }
        )
    }

    Setting(
        modifier = modifier.clickable { showDialog = true },
        text = label,
        description = description,
        icon = icon,
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