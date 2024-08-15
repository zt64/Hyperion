package dev.zt64.hyperion.ui.component

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR

@Stable
actual class ShareButtonState(private val context: Context) {
    actual fun share(content: String, label: String?) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"

            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_TITLE, label)
        }

        context.startActivity(
            Intent.createChooser(shareIntent, null).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }
}

@Composable
actual fun rememberShareButtonState(): ShareButtonState {
    val context = LocalContext.current

    return remember {
        ShareButtonState(context)
    }
}

@Composable
internal actual fun ShareButtonIcon() {
    Icon(
        imageVector = Icons.Default.Share,
        contentDescription = stringResource(MR.strings.share)
    )
}

@Composable
internal actual fun ShareButtonText() {
    Text(stringResource(MR.strings.share))
}