package dev.zt64.hyperion.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR

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