package com.hyperion.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.domain.model.DomainSearch
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistCard(
    modifier: Modifier = Modifier,
    playlist: DomainSearch.Result.Playlist,
    onClick: () -> Unit,
    prefs: PreferencesManager = get()
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        onClick = onClick
    ) {
        val compactCard = (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) || prefs.compactCard

        if (compactCard) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Thumbnail(
                    modifier = Modifier.width(140.dp),
                    thumbnailUrl = playlist.thumbnailUrl,
                    videoCountText = playlist.videoCountText
                )

                Column(
                    modifier = Modifier
                        .heightIn(min = 70.dp)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = playlist.title,
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = playlist.channelName,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodySmall
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
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column {
                        Text(
                            text = playlist.title,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 2,
                        )

                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = playlist.channelName,
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
    thumbnailUrl: String,
    videoCountText: String
) {
    Box(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .then(modifier)
    ) {
        AsyncImage(
            modifier = Modifier.aspectRatio(16f / 9f),
            model = thumbnailUrl,
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
                imageVector = Icons.Default.PlaylistPlay,
                contentDescription = null
            )

            Text(
                text = videoCountText,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}