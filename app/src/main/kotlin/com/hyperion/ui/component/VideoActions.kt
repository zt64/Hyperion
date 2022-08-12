package com.hyperion.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R

@Composable
fun VideoActions(
    modifier: Modifier = Modifier,
    voteEnabled: Boolean,
    likeLabel: @Composable () -> Unit,
    dislikeLabel: @Composable () -> Unit,
    onClickVote: (Boolean) -> Unit,
    onClickShare: () -> Unit,
    onClickDownload: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalToggleButton(
            enabled = voteEnabled,
            checked = false,
            onCheckedChange = onClickVote
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = stringResource(R.string.like)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            ProvideTextStyle(value = MaterialTheme.typography.labelMedium) {
                likeLabel()
            }
        }

        FilledTonalToggleButton(
            enabled = voteEnabled,
            checked = false,
            onCheckedChange = onClickVote
        ) {
            Icon(
                imageVector = Icons.Default.ThumbDown,
                contentDescription = stringResource(R.string.dislike)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            ProvideTextStyle(value = MaterialTheme.typography.labelMedium) {
                dislikeLabel()
            }
        }

        Spacer(Modifier.weight(1f, true))

        FilledTonalIconButton(onClick = onClickShare) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(R.string.share)
            )
        }

        FilledTonalIconButton(
            enabled = false,
            onClick = onClickDownload
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = stringResource(R.string.download)
            )
        }
    }
}