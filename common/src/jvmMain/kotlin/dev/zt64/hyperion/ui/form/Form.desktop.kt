package dev.zt64.hyperion.ui.form

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR

@Composable
actual fun Form(
    onDismissRequest: () -> Unit,
    title: @Composable (() -> Unit)?,
    modifier: Modifier,
    properties: FormProperties,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        text = {
            CompositionLocalProvider(
                LocalAbsoluteTonalElevation provides 8.dp
            ) {
                content()
            }
        },
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text(stringResource(MR.strings.done))
            }
        },
        modifier = modifier,
        tonalElevation = 4.dp,
        properties = DialogProperties(
            dismissOnBackPress = properties.shouldDismissOnBackPress
        )
    )
}