package dev.zt64.hyperion.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlaylistAddCircle
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.api.domain.model.DomainChannelPartial
import dev.zt64.hyperion.api.domain.model.DomainVideoPartial
import dev.zt64.hyperion.api.model.Video
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.component.player.WIDESCREEN_RATIO
import dev.zt64.hyperion.ui.menu.ContextMenu
import dev.zt64.hyperion.ui.menu.ContextMenuItem
import dev.zt64.hyperion.ui.screen.ChannelScreen
import dev.zt64.hyperion.ui.screen.PlayerScreen
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import org.koin.compose.koinInject

@Composable
fun VideoCard(video: Video, modifier: Modifier = Modifier, onLongClick: () -> Unit = { }) {
    val navController = LocalNavigator.currentOrThrow

    // VideoCard(
    //     video = video,
    //     onClick = {
    //         navController.push(PlayerScreen(video.id))
    //     },
    //     onLongClick = onLongClick,
    //     modifier = modifier
    // )
}

@Composable
fun VideoCard(video: DomainVideoPartial, modifier: Modifier = Modifier, onLongClick: () -> Unit = { }) {
    val navController = LocalNavigator.currentOrThrow

    VideoCard(
        video = video,
        onClick = {
            navController.push(PlayerScreen(video.id))
        },
        modifier = modifier,
        onLongClick = onLongClick
    )
}

@Composable
fun VideoCard(video: DomainVideoPartial, onClick: () -> Unit, modifier: Modifier = Modifier, onLongClick: () -> Unit = { }) {
    val navController = LocalNavigator.currentOrThrow

    VideoCard(
        video = video,
        onClick = onClick,
        onClickChannel = {
            navController.push(ChannelScreen(video.channel!!.id))
        },
        onLongClick = onLongClick,
        modifier = modifier
    )
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
                publishedTimeText = "1 year ago"
            ),
            onClick = {},
            onClickChannel = {},
            onLongClick = {}
        )
    }
}

@Composable
fun VideoCard(
    video: DomainVideoPartial,
    onClick: () -> Unit,
    onClickChannel: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ContextMenu(
        menuItems = {
            ContextMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.WatchLater,
                        contentDescription = stringResource(MR.strings.save_to_watch_later)
                    )
                },
                text = { Text(stringResource(MR.strings.save_to_watch_later)) },
                onClick = { }
            )
            ContextMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PlaylistAddCircle,
                        contentDescription = stringResource(MR.strings.save_to_playlist)
                    )
                },
                text = { Text(stringResource(MR.strings.save_to_playlist)) },
                onClick = { }
            )

            ContextMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = stringResource(MR.strings.download)
                    )
                },
                text = { Text(stringResource(MR.strings.download)) },
                onClick = { }
            )

            ContextMenuShareItem(video.title)
        }
    ) {
        PlainTooltipBox(
            tooltip = {
                PlainTooltip {
                    Text(video.title)
                }
            }
        ) {
            ElevatedCard(
                modifier = modifier,
                onClick = onClick,
                onLongClick = onLongClick
            ) {
                val prefs: PreferencesManager = koinInject()
                val windowSizeClass = LocalWindowSizeClass.current

                if (windowSizeClass.widthSizeClass > WindowWidthSizeClass.Medium &&
                    windowSizeClass.heightSizeClass <= WindowHeightSizeClass.Medium
                ) {
                    // Compact horizontal layout
                    Row(
                        modifier = Modifier.fillMaxWidth()
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
                    Column(
                        modifier = Modifier.width(360.dp)
                    ) {
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
    }
}

@Composable
fun VideoCard(video: Video, onClick: () -> Unit, onClickChannel: () -> Unit, onLongClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick
    ) {
        val prefs: PreferencesManager = koinInject()
        val windowSizeClass = LocalWindowSizeClass.current

        if (windowSizeClass.widthSizeClass > WindowWidthSizeClass.Medium &&
            windowSizeClass.heightSizeClass <= WindowHeightSizeClass.Medium
        ) {
            // Compact horizontal layout
            Row(
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                // Thumbnail(
                //     modifier = Modifier.width(160.dp),
                //     video = video,
                //     timeStampScale = prefs.timestampScale,
                // )

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
                        // Avatar(
                        //     modifier = Modifier.size(28.dp),
                        //     channel = video.channel,
                        //     onClick = onClickChannel,
                        // )
                        //
                        // Text(
                        //     text = video.description,
                        //     style = MaterialTheme.typography.labelSmall,
                        //     overflow = TextOverflow.Ellipsis,
                        //     maxLines = 2,
                        // )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.width(360.dp)
            ) {
                // Thumbnail(
                //     video = video,
                //     timeStampScale = prefs.timestampScale,
                // )

                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar(
                    //     modifier = Modifier.size(38.dp),
                    //     channel = video.channel,
                    //     onClick = onClickChannel,
                    // )
                    //
                    // Column {
                    //     Text(
                    //         text = video.title,
                    //         style = MaterialTheme.typography.labelLarge,
                    //         overflow = TextOverflow.Ellipsis,
                    //         maxLines = 2,
                    //     )
                    //
                    //     Spacer(Modifier.height(2.dp))
                    //
                    //     Text(
                    //         text = video.subtitle,
                    //         style = MaterialTheme.typography.labelSmall,
                    //         overflow = TextOverflow.Ellipsis,
                    //         maxLines = 2,
                    //     )
                    // }
                }
            }
        }
    }
}

@Composable
private fun Avatar(channel: DomainChannelPartial?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    channel?.avatarUrl?.let {
        ShimmerImage(
            modifier = modifier.clip(CircleShape).clickable(onClick = onClick),
            url = it,
            contentDescription = channel.name
        )
    }
}

@Composable
private fun Thumbnail(video: DomainVideoPartial, timeStampScale: Float, modifier: Modifier = Modifier) {
    Box(modifier) {
        ShimmerImage(
            modifier = Modifier.aspectRatio(WIDESCREEN_RATIO),
            url = video.thumbnailUrl,
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(MR.strings.thumbnail)
        )

        Surface(
            modifier = Modifier.padding(8.dp).align(Alignment.BottomEnd),
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