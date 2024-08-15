package dev.zt64.hyperion.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.LocalSnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
actual class ShareButtonState(
    private val clipboardManager: ClipboardManager,
    private val snackbarHostState: SnackbarHostState?
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    actual fun share(content: String, label: String?) {
        scope.launch {
            clipboardManager.setText(AnnotatedString(content))
            snackbarHostState?.showSnackbar("Copied to clipboard")
        }
    }
}

@Composable
actual fun rememberShareButtonState(): ShareButtonState {
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = LocalSnackbarHostState.current

    return remember {
        ShareButtonState(clipboardManager, snackbarHostState)
    }
}

@Composable
internal actual fun ShareButtonIcon() {
    Icon(
        imageVector = Icons.Default.ContentCopy,
        contentDescription = null
    )
}

@Composable
internal actual fun ShareButtonText() {
    Text(stringResource(MR.strings.share))
}