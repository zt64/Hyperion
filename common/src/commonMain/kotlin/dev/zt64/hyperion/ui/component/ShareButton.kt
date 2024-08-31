package dev.zt64.hyperion.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import dev.zt64.hyperion.ui.menu.ContextMenuScope

@Stable
expect class ShareButtonState {
    fun share(content: String, label: String?)
}

@Composable
expect fun rememberShareButtonState(): ShareButtonState

/**
 * An icon button that shares the given content on click.
 * On Android, this will open the system share sheet.
 * On Desktop, this will copy the content to the clipboard.
 */
@Composable
fun ShareIconButton(content: String, modifier: Modifier = Modifier, label: String? = null) {
    val state = rememberShareButtonState()

    IconButton(
        modifier = modifier,
        onClick = { state.share(content, label) },
        content = { ShareButtonIcon() }
    )
}

/**
 * A filled tonal icon button that shares the given content on click.
 * On Android, this will open the system share sheet.
 * On Desktop, this will copy the content to the clipboard.
 */
@Composable
fun FilledTonalShareIconButton(content: String, modifier: Modifier = Modifier, label: String? = null) {
    val state = rememberShareButtonState()

    FilledTonalIconButton(
        modifier = modifier,
        onClick = { state.share(content, label) },
        content = { ShareButtonIcon() }
    )
}

/**
 * A button that shares the given content on click.
 * On Android, this will open the system share sheet.
 * On Desktop, this will copy the content to the clipboard.
 */
@Composable
fun ShareButton(content: String, modifier: Modifier = Modifier, label: String? = null) {
    val state = rememberShareButtonState()

    Button(
        modifier = modifier,
        onClick = { state.share(content, label) },
        content = {
            ShareButtonIcon()
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            ShareButtonText()
        }
    )
}

@Composable
fun ContextMenuScope.ContextMenuShareItem(content: String, modifier: Modifier = Modifier, label: String? = null) {
    val state = rememberShareButtonState()

    DropdownMenuItem(
        modifier = modifier,
        leadingIcon = { ShareButtonIcon() },
        text = { ShareButtonText() },
        onClick = {
            state.share(content, label)
            close()
        }
    )
}

@Composable
internal expect fun ShareButtonIcon()

@Composable
internal expect fun ShareButtonText()