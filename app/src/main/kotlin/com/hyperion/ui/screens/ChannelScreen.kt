package com.hyperion.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hyperion.R
import com.hyperion.model.Channel
import com.hyperion.network.service.InvidiousService
import com.hyperion.ui.screens.destinations.PlayerScreenDestination
import com.hyperion.util.toCompact
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Destination
@Composable
fun ChannelScreen(
    navigator: DestinationsNavigator,
    channelId: String
) {
    var channel by rememberSaveable { mutableStateOf<Channel?>(null) }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) { channel = InvidiousService.getChannel(channelId) }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (channel != null) {
            item {
                Column {
                    AsyncImage(
                        modifier = Modifier.fillMaxWidth(),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(channel!!.authorBanners.last().url)
                            .crossfade(true)
                            .build(),
                        contentDescription = null
                    )

                    Row(
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(52.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(channel!!.authorThumbnails.last().url)
                                .crossfade(true)
                                .build(),
                            contentDescription = null
                        )

                        Column {
                            Text(
                                text = channel!!.author,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${channel!!.subCount.toCompact()} ${stringResource(R.string.subscribers)}",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "${channel!!.totalViews.toCompact()} ${stringResource(R.string.views)}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f, true))

                        FilledTonalButton(
                            onClick = { /* TODO */ }
                        ) {
                            Text(stringResource(R.string.subscribe))
                        }
                    }
                }
            }
        }

        stickyHeader {
            var selectedTabIndex by remember { mutableStateOf(0) }

            val tabs = mapOf(
                stringResource(R.string.home_screen) to Icons.Default.Home,
                stringResource(R.string.videos_screen) to Icons.Default.VideoLibrary,
                stringResource(R.string.playlists_screen) to Icons.Default.ViewList
            )

            TabRow(
                selectedTabIndex = selectedTabIndex,
                tabs = {
                    tabs.entries.forEachIndexed { index, (title, icon) ->
                        Tab(
                            selected = selectedTabIndex == index,
                            text = { Text(title) },
                            icon = { Icon(icon, null) },
                            onClick = { selectedTabIndex = index }
                        )
                    }
                }
            )
        }

        items(channel?.latestVideos ?: emptyList()) { video ->
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .clickable {
                        navigator.navigate(PlayerScreenDestination(video.videoId))
                    }
            ) {
                Box {
                    AsyncImage(
                        modifier = Modifier.aspectRatio(16f / 9f),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(video.videoThumbnails.first { it.quality == "medium" }.url)
                            .crossfade(true)
                            .build(),
                        contentDescription = stringResource(R.string.thumbnail)
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f))
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
                    Column {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2
                        )

                        Text(
                            text = buildString {
                                append(video.viewCount.toCompact())
                                append(" ${stringResource(R.string.views)} - ")
                                append(video.publishedText)
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}