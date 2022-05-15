package com.hyperion.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hyperion.R
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.preferences.Prefs
import com.hyperion.preferences.VideoCardStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCard(
    modifier: Modifier = Modifier,
    video: DomainVideoPartial,
    onChannelClick: () -> Unit = { },
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE || Prefs.videoCardStyle == VideoCardStyle.COMPACT) {
            // Compact horizontal layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Thumbnail(
                    modifier = Modifier.width(140.dp),
                    video = video
                )

                Column(
                    modifier = Modifier
                        .heightIn(min = 70.dp)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.labelMedium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        video.author?.avatarUrl?.let {
                            ChannelThumbnail(
                                modifier = Modifier
                                    .clickable(onClick = onChannelClick)
                                    .size(28.dp),
                                url = it
                            )
                        }

                        Text(
                            text = video.subtitle,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        } else {
            Column {
                Thumbnail(video = video)

                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    video.author?.avatarUrl?.let {
                        ChannelThumbnail(
                            modifier = Modifier
                                .clickable(onClick = onChannelClick)
                                .size(36.dp),
                            url = it
                        )
                    }

                    Column {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 2,
                        )

                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = video.subtitle,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Thumbnail(
    modifier: Modifier = Modifier,
    video: DomainVideoPartial
) {
    Box(modifier) {
        AsyncImage(
            modifier = Modifier.aspectRatio(16f / 9f),
            model = ImageRequest.Builder(LocalContext.current)
                .data(video.thumbnailUrl)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.thumbnail),
            contentScale = ContentScale.Crop
        )

        if (video.timestamp != null) {
            Timestamp(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = video.timestamp
            )
        }
    }
}