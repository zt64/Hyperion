package dev.zt64.hyperion.ui.component

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR

@Composable
actual fun ShareButton(
    content: String,
    modifier: Modifier,
    label: String?
) {
    val context = LocalContext.current

    IconButton(
        onClick = {
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
        },
        content = { ShareButtonIcon() }
    )
}

@Composable
actual fun FilledTonalShareButton(
    content: String,
    label: String?
) {
    val context = LocalContext.current

    FilledTonalIconButton(
        onClick = {
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
        },
        content = { ShareButtonIcon() }
    )
}

@Composable
internal actual fun ShareButtonIcon() {
    Icon(
        imageVector = Icons.Default.Share,
        contentDescription = stringResource(MR.strings.share)
    )
}