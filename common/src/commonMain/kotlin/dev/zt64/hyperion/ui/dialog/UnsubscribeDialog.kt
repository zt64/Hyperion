package dev.zt64.hyperion.ui.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR

@Composable
fun UnsubscribeDialog(onConfirm: () -> Unit, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(stringResource(MR.strings.unsubscribe))
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text(stringResource(MR.strings.cancel))
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(MR.strings.unsubscribe))
            }
        }
    )
}

@Preview
@Composable
private fun UnsubscribeDialogPreview() {
    UnsubscribeDialog(
        onConfirm = {},
        onDismissRequest = {}
    )
}