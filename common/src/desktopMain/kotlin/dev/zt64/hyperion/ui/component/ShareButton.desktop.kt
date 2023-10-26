package dev.zt64.hyperion.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import dev.zt64.hyperion.ui.LocalSnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
private class ShareButtonState(
    private val content: String,
    private val clipboardManager: ClipboardManager,
    private val snackbarHostState: SnackbarHostState?
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun share() {
        scope.launch {
            clipboardManager.setText(AnnotatedString(content))
            snackbarHostState?.showSnackbar("Copied to clipboard")
        }
    }
}

@Composable
private fun rememberShareButtonState(
    content: String,
    clipboardManager: ClipboardManager = LocalClipboardManager.current,
    snackbarHostState: SnackbarHostState? = LocalSnackbarHostState.current
): ShareButtonState {
    return remember(content) {
        ShareButtonState(content, clipboardManager, snackbarHostState)
    }
}

@Composable
actual fun ShareButton(content: String, label: String?) {
    val state = rememberShareButtonState(content)

    IconButton(
        onClick = state::share,
        content = { ShareButtonIcon() }
    )
}

@Composable
actual fun FilledTonalShareButton(content: String, label: String?) {
    val state = rememberShareButtonState(content)

    FilledTonalIconButton(
        onClick = state::share,
        content = { ShareButtonIcon() }
    )
}

@Composable
internal actual fun ShareButtonIcon() {
    Icon(
        imageVector = Icons.Default.ContentCopy,
        contentDescription = null
    )
}