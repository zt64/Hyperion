package com.hyperion.ui.components

import android.icu.text.NumberFormat
import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hyperion.model.TrendingVideo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCard(
    onChannelClick: () -> Unit,
    onClick: () -> Unit,
    video: TrendingVideo
) {
    ElevatedCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box {
            AsyncImage(
                modifier = Modifier.aspectRatio(16f/9f),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.videoThumbnails.first { it.quality == "medium" }.url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Thumbnail"
            )

            Text(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
                    .padding(2.dp),
                text = DateUtils.formatElapsedTime(video.lengthSeconds.toLong()),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChannelThumbnail(
                modifier = Modifier.clickable(onClick = onChannelClick),
                authorId = video.authorId
            )

            Column {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )

                Text(
                    text = buildString {
                        append("${video.author} - ")
                        append("${NumberFormat.getInstance().format(video.viewCount)} views - ")
                        append(video.publishedText)
                    },
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}