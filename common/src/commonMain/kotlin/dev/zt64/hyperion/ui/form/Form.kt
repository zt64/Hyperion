package dev.zt64.hyperion.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// @Composable
// fun Form(
//     onDismissRequest: () -> Unit,
//     label: @Composable () -> Unit,
//     content: @Composable () -> Unit,
// ) {
//
// }

@Composable
expect fun Form(
    onDismissRequest: () -> Unit,
    label: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
)