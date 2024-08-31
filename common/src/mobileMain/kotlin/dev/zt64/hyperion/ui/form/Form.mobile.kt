package dev.zt64.hyperion.ui.form

import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun Form(
    onDismissRequest: () -> Unit,
    title: @Composable (() -> Unit)?,
    modifier: Modifier,
    properties: FormProperties,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        content = {
            CompositionLocalProvider {
                content()
            }
        },
        tonalElevation = 8.dp,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = properties.shouldDismissOnBackPress
        ),
        modifier = modifier
    )
}