package com.hyperion.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.util.shimmer
import org.koin.androidx.compose.get

@Composable
fun VideoCard(
    modifier: Modifier = Modifier,
    video: DomainVideoPartial,
    onClick: () -> Unit,
    onClickChannel: () -> Unit = { },
    onLongClick: () -> Unit = { },
    prefs: PreferencesManager = get()
) {
    ElevatedCard(
        modifier = modifier
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        val orientation = LocalConfiguration.current.orientation

        if (orientation == Configuration.ORIENTATION_LANDSCAPE || prefs.compactCard) {
            // Compact horizontal layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Thumbnail(
                    modifier = Modifier.width(160.dp),
                    video = video,
                    timeStampScale = prefs.timestampScale
                )

                Column(
                    modifier = Modifier
                        .heightIn(min = 70.dp)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.labelMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        video.channel?.avatarUrl?.let {
                            ChannelThumbnail(
                                modifier = Modifier
                                    .clickable(onClick = onClickChannel)
                                    .size(28.dp),
                                url = it
                            )
                        }

                        Text(
                            text = video.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    }
                }
            }
        } else {
            Column {
                Thumbnail(
                    video = video,
                    timeStampScale = prefs.timestampScale
                )

                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    video.channel?.avatarUrl?.let {
                        ChannelThumbnail(
                            modifier = Modifier
                                .clickable(onClick = onClickChannel)
                                .size(36.dp),
                            url = it
                        )
                    }

                    Column {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 2
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
    video: DomainVideoPartial,
    timeStampScale: Float
) {
    Box(modifier) {
        SubcomposeAsyncImage(
            modifier = Modifier.aspectRatio(16f / 9f),
            model = video.thumbnailUrl,
            loading = {
                Box(
                    modifier = modifier
                        .shimmer()
                        .fillMaxSize()
                )
            },
            success = {
                SubcomposeAsyncImageContent()
            },
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(R.string.thumbnail)
        )

        if (video.timestamp != null) {
            Surface(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = video.timestamp,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontSize = 14.sp * timeStampScale
                )
            }
        }
    }
}