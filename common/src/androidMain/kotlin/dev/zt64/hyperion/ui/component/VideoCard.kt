package dev.zt64.hyperion.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import dev.olshevski.navigation.reimagined.navigate
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.component.player.WIDESCREEN_RATIO
import dev.zt64.hyperion.ui.navigation.AppDestination
import dev.zt64.hyperion.ui.navigation.LocalNavController
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import dev.zt64.innertube.domain.model.DomainChannelPartial
import dev.zt64.innertube.domain.model.DomainVideoPartial
import org.koin.compose.koinInject

@Composable
fun VideoCard(
    video: DomainVideoPartial,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = { },
) {
    val navController = LocalNavController.current

    VideoCard(
        video = video,
        onClick = {
            navController.navigate(AppDestination.Player(video.id))
        },
        onLongClick = onLongClick,
        modifier = modifier,
    )
}


@Composable
fun VideoCard(
    video: DomainVideoPartial,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit = { },
) {
    val navController = LocalNavController.current

    VideoCard(
        video = video,
        onClick = onClick,
        onClickChannel = {
            navController.navigate(AppDestination.Channel(video.channel!!.id))
        },
        onLongClick = onLongClick,
        modifier = modifier,
    )
}

@Composable
fun VideoCard(
    video: DomainVideoPartial,
    onClick: () -> Unit,
    onClickChannel: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prefs: PreferencesManager = koinInject()
    val windowSizeClass = LocalWindowSizeClass.current
    val navController = LocalNavController.current

    ElevatedCard(
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick
    ) {
        val orientation = LocalConfiguration.current.orientation

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
                    modifier = Modifier.padding(12.dp),
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
                        Avatar(
                            modifier = Modifier.size(28.dp),
                            channel = video.channel,
                            onClick = onClickChannel
                        )

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
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Avatar(
                        modifier = Modifier.size(38.dp),
                        channel = video.channel,
                        onClick = onClickChannel
                    )

                    Column {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.labelLarge,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = video.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Avatar(
    modifier: Modifier = Modifier,
    channel: DomainChannelPartial?,
    onClick: () -> Unit
) {
    channel?.avatarUrl?.let {
        ShimmerImage(
            modifier = modifier
                .clip(CircleShape)
                .clickable(onClick = onClick),
            url = it,
            contentDescription = channel.name
        )
    }
}

@Composable
private fun Thumbnail(
    video: DomainVideoPartial,
    timeStampScale: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        ShimmerImage(
            modifier = Modifier.aspectRatio(WIDESCREEN_RATIO),
            url = video.thumbnailUrl,
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(MR.strings.thumbnail)
        )

        Surface(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomEnd),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = video.timestamp ?: stringResource(MR.strings.live),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontSize = 14.sp * timeStampScale
            )
        }
    }
}

@Preview
@Composable
private fun VideoCardPreview() {
    HyperionPreview {
        VideoCard(
            video = DomainVideoPartial(
                id = "id",
                title = "Video title",
                viewCount = "1.2M",
                publishedTimeText = "1 year ago",
            )
        )
    }
}