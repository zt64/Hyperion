package com.hyperion.ui.components.player

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.model.Video

@Composable
fun VideoActions(
    video: Video
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceAround
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onBackground
    ) {
        val context = LocalContext.current

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {  /* TODO */ }
            ) {
                Icon(imageVector = Icons.Default.ThumbUp, contentDescription = stringResource(R.string.like))
            }
            Text(
                text = video.likeCount.toString(),
                style = MaterialTheme.typography.labelMedium
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {  /* TODO */ }
            ) {
                Icon(imageVector = Icons.Default.ThumbDown, contentDescription = stringResource(R.string.dislike))
            }

            Text(
                text = stringResource(R.string.todo),
                style = MaterialTheme.typography.labelMedium
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"

                        putExtra(Intent.EXTRA_TEXT, video.hlsUrl)
                        putExtra(Intent.EXTRA_TITLE, video.title)
                    }

                    context.startActivity(Intent.createChooser(shareIntent, null))
                }
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
                onClick = {
                    /* TODO: Download video */
                }
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