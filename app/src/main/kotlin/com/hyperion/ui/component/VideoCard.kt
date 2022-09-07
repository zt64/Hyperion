package com.hyperion.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.domain.model.DomainVideoPartial
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCard(
    modifier: Modifier = Modifier,
    video: DomainVideoPartial,
    onClick: () -> Unit,
    onClickChannel: () -> Unit = { },
    prefs: PreferencesManager = get()
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick
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
                    modifier = Modifier.width(140.dp),
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
                        style = MaterialTheme.typography.labelMedium
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
                            style = MaterialTheme.typography.labelSmall
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
    video: DomainVideoPartial,
    timeStampScale: Float
) {
    Box(modifier) {
        SubcomposeAsyncImage(
            modifier = Modifier.aspectRatio(16f / 9f),
            model = video.thumbnailUrl,
            loading = {
                val localElevation = LocalAbsoluteTonalElevation.current

                Box(
                    modifier = modifier
                        .placeholder(
                            visible = true,
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(localElevation + 2.dp),
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = MaterialTheme.colorScheme.surfaceColorAtElevation(localElevation + 3.dp)
                            )
                        )
                        .fillMaxSize(),
                )
            },
            success = {
                SubcomposeAsyncImageContent()
            },
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(R.string.thumbnail),
        )

        if (video.timestamp != null) {
            Timestamp(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = video.timestamp,
                scale = timeStampScale
            )
        }
    }
}

@Composable
fun Timestamp(
    modifier: Modifier = Modifier,
    text: String,
    scale: Float
) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .then(modifier),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontSize = 14.sp * scale
        )
    }
}