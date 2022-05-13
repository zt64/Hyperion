package com.hyperion.ui.components.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.domain.model.DomainVideo

@Composable
fun VideoActions(
    video: DomainVideo,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceAround
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onLike
            ) {
                Icon(imageVector = Icons.Default.ThumbUp, contentDescription = stringResource(R.string.like))
            }

            Text(
                text = video.likesText,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onDislike
            ) {
                Icon(imageVector = Icons.Default.ThumbDown, contentDescription = stringResource(R.string.dislike))
            }

            Text(
                text = video.dislikes.toString(),
                style = MaterialTheme.typography.labelMedium
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onShare
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = stringResource(R.string.share))
            }

            Text(
                text = stringResource(R.string.share),
                style = MaterialTheme.typography.labelMedium
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onDownload
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = stringResource(R.string.download))
            }

            Text(
                text = stringResource(R.string.download),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}