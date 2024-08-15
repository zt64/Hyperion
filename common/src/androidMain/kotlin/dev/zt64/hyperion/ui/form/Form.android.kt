package dev.zt64.hyperion.ui.form

import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun Form(
    onDismissRequest: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        content = { content() },
        modifier = modifier
    )
}