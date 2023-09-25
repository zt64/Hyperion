package dev.zt64.hyperion.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.navigate
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.component.player.WIDESCREEN_RATIO
import dev.zt64.hyperion.ui.navigation.AppDestination
import dev.zt64.hyperion.ui.navigation.LocalNavController
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import dev.zt64.innertube.domain.model.DomainPlaylistPartial
import org.koin.compose.koinInject

@Composable
fun PlaylistCard(
    playlist: DomainPlaylistPartial,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = { },
    prefs: PreferencesManager = koinInject()
) {
    val navController = LocalNavController.current
    val windowSizeClass = LocalWindowSizeClass.current

    ElevatedCard(
        modifier = modifier
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    navController.navigate(AppDestination.Playlist(playlist.id))
                },
                onLongClick = onLongClick
            )
    ) {
        val orientation = LocalConfiguration.current.orientation

        if (orientation == Configuration.ORIENTATION_LANDSCAPE || prefs.compactCard) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Thumbnail(
                    modifier = Modifier.width(160.dp),
                    thumbnailUrl = playlist.thumbnailUrl,
                    videoCountText = playlist.videoCountText
                )

                Column(
                    modifier = Modifier.padding(12.dp),
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
                Thumbnail(
                    thumbnailUrl = playlist.thumbnailUrl,
                    videoCountText = playlist.videoCountText
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
private fun Thumbnail(
    modifier: Modifier = Modifier,
    thumbnailUrl: String,
    videoCountText: String
) {
    Box(
        modifier = Modifier
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
            modifier = Modifier
                .width(72.dp)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .background(color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
        ) {
            Icon(
                modifier = Modifier.size(34.dp),
                imageVector = Icons.AutoMirrored.Default.PlaylistPlay,
                contentDescription = null
            )

            Text(
                text = videoCountText,
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
            playlist = DomainPlaylistPartial(
                id = "playlistId",
                title = "Playlist Title",
                subtitle = "Playlist Subtitle",
                thumbnailUrl = "https://i.ytimg.com/vi/0qUW56RQzRw/hqdefault.jpg",
                videoCountText = "10 videos"
            )
        )
    }
}