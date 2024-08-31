package dev.zt64.hyperion.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class FormProperties(val shouldDismissOnBackPress: Boolean = true)

/**
 * A form that can be used to collect user input. The form can be dismissed by the user by pressing the back button.
 * On Android, the form is a sheet that slides up from the bottom of the screen.
 * On desktop, the form is a dialog that appears in the center of the screen.
 *
 * @param onDismissRequest
 * @param title
 * @param modifier
 * @param properties
 * @param content
 */
@Composable
expect fun Form(
    onDismissRequest: () -> Unit,
    title: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    properties: FormProperties = FormProperties(),
    content: @Composable () -> Unit
)