package dev.zt64.hyperion.ui.component.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.domain.model.Rating
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.FilledTonalShareIconButton

@Composable
fun PlayerActions(
    voteEnabled: Boolean,
    likeLabel: @Composable () -> Unit,
    dislikeLabel: @Composable () -> Unit,
    showDownloadButton: Boolean,
    onClickVote: (Rating) -> Unit,
    onClickShare: () -> Unit,
    onClickDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalButton(
            enabled = voteEnabled,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = stringResource(MR.strings.like)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            ProvideTextStyle(MaterialTheme.typography.labelMedium) {
                likeLabel()
            }
        }

        FilledTonalButton(
            enabled = voteEnabled,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Default.ThumbDown,
                contentDescription = stringResource(MR.strings.dislike)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            ProvideTextStyle(MaterialTheme.typography.labelMedium) {
                dislikeLabel()
            }
        }

        Spacer(Modifier.weight(1f, true))

        FilledTonalShareIconButton("")

        if (showDownloadButton) {
            FilledTonalIconButton(onClick = onClickDownload) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(MR.strings.download)
                )
            }
        }
    }
}