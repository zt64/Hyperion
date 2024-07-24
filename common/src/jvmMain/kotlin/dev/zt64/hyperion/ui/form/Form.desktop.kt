package dev.zt64.hyperion.ui.form

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun Form(
    onDismissRequest: () -> Unit,
    label: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = label,
        text = content,
        confirmButton = {
            Button(onClick = {}) {
                Text("Done")
            }
        },
        modifier = modifier
    )
}