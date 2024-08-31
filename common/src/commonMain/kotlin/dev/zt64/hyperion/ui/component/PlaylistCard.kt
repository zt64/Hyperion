package dev.zt64.hyperion.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.zt64.hyperion.api.domain.model.DomainPlaylistPartial
import dev.zt64.hyperion.api.model.Playlist
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.component.player.WIDESCREEN_RATIO
import dev.zt64.hyperion.ui.screen.PlaylistScreen
import dev.zt64.hyperion.ui.tooling.HyperionPreview

@Composable
fun PlaylistCard(playlist: Playlist, modifier: Modifier = Modifier, onLongClick: () -> Unit = { }) {
    val navController = LocalNavigator.currentOrThrow

    PlaylistCard(
        playlist = playlist,
        onClick = { navController.push(PlaylistScreen(playlist.id)) },
        onLongClick = onLongClick,
        modifier = modifier
    )
}

@Composable
fun PlaylistCard(playlist: DomainPlaylistPartial, modifier: Modifier = Modifier, onLongClick: () -> Unit = { }) {
    val navController = LocalNavigator.currentOrThrow

    PlaylistCard(
        playlist = playlist,
        onClick = { navController.push(PlaylistScreen(playlist.id)) },
        onLongClick = onLongClick,
        modifier = modifier
    )
}

@Composable
fun PlaylistCard(playlist: DomainPlaylistPartial, onClick: () -> Unit, onLongClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick
    ) {
        val windowSizeClass = LocalWindowSizeClass.current

        if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
//                Thumbnail(
//                    modifier = Modifier.width(160.dp),
//                    thumbnailUrl = playlist.thumbnailUrl,
//                    videoCountText = playlist.videoCountText
//                )

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = playlist.title,
                        style = MaterialTheme.typography.labelMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = playlist.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }
        } else {
            Column {
                // Thumbnail(
                //     thumbnailUrl = playlist.thumbnailUrl,
                //     videoCountText = playlist.videoCountText
                // )

                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Text(
                            text = playlist.title,
                            style = MaterialTheme.typography.labelLarge,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = playlist.subtitle,
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
fun PlaylistCard(playlist: Playlist, onClick: () -> Unit, onLongClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick
    ) {
        val windowSizeClass = LocalWindowSizeClass.current

        if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Thumbnail(
                    modifier = Modifier.width(160.dp),
                    thumbnailUrl = playlist.thumbnails.default.url,
                    videoCountText = playlist.itemCount.toInt()
                )

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = playlist.title,
                        style = MaterialTheme.typography.labelMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )

                    Spacer(Modifier.height(2.dp))

                    // Text(
                    //     text = playlist.description,
                    //     style = MaterialTheme.typography.labelSmall,
                    //     overflow = TextOverflow.Ellipsis,
                    //     maxLines = 2
                    // )
                }
            }
        } else {
            Column {
                Thumbnail(
                    thumbnailUrl = playlist.thumbnails.default.url,
                    videoCountText = playlist.itemCount.toInt()
                )

                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Text(
                            text = playlist.title,
                            style = MaterialTheme.typography.labelLarge,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = playlist.channelTitle,
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
private fun Thumbnail(thumbnailUrl: String, videoCountText: Int, modifier: Modifier = Modifier) {
    Box(
        modifier =
        Modifier
            .height(IntrinsicSize.Min)
            .then(modifier)
    ) {
        ShimmerImage(
            modifier = Modifier.aspectRatio(WIDESCREEN_RATIO),
            url = thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Column(
            modifier =
            Modifier
                .width(72.dp)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
        ) {
            Icon(
                modifier = Modifier.size(34.dp),
                imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                contentDescription = null
            )

            Text(
                text = videoCountText.toString(),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview
@Composable
private fun PlaylistCardPreview() {
    HyperionPreview {
        PlaylistCard(
            playlist =
            DomainPlaylistPartial(
                id = "playlistId",
                title = "Playlist Title",
                subtitle = "Playlist Subtitle",
                thumbnailUrl = "https://i.ytimg.com/vi/0qUW56RQzRw/hqdefault.jpg",
                videoCountText = "10 videos"
            )
        )
    }
}