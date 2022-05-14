package com.hyperion.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hyperion.R
import com.hyperion.ui.components.Timestamp
import com.hyperion.ui.screens.destinations.PlayerScreenDestination
import com.hyperion.ui.viewmodel.ChannelViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

enum class ChannelTab(
    @StringRes
    val title: Int,
    val imageVector: ImageVector
) {
    Home(R.string.home, Icons.Default.Home),
    Videos(R.string.videos, Icons.Default.VideoLibrary),
    Playlists(R.string.playlists, Icons.Default.ViewList)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Destination
@Composable
fun ChannelScreen(
    navigator: DestinationsNavigator,
    viewModel: ChannelViewModel = hiltViewModel(),
    channelId: String
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val state = viewModel.state

        LaunchedEffect(Unit) {
            viewModel.getChannel(channelId)
        }

        when (state) {
            is ChannelViewModel.State.Loaded -> {
                var selectedTab by remember { mutableStateOf(ChannelTab.Home) }
                val channel = state.channel

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Column {
                            AsyncImage(
                                modifier = Modifier.fillMaxWidth(),
                                model = channel.banner,
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
                                    model = channel.avatar,
                                    contentDescription = null
                                )

                                Column(
                                    modifier = Modifier.weight(1f, true)
                                ) {
                                    Text(
                                        text = channel.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    channel.subscriberText?.let { subscriberText ->
                                        Text(
                                            text = subscriberText,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }

                                FilledTonalButton(
                                    onClick = { /* TODO */ }
                                ) {
                                    Text(stringResource(R.string.subscribe))
                                }
                            }
                        }
                    }

                    stickyHeader {
                        TabHeader(
                            selectedTab = selectedTab,
                            onClick = { selectedTab = it }
                        )
                    }

                    when (selectedTab) {
                        ChannelTab.Home -> {
                            items(channel.videos) { video ->
                                ElevatedCard(
                                    modifier = Modifier
                                        .padding(horizontal = 14.dp)
                                        .clickable { navigator.navigate(PlayerScreenDestination(video.id)) }
                                ) {
                                    Box {
                                        AsyncImage(
                                            modifier = Modifier.aspectRatio(16f / 9f),
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(video.thumbnailUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentScale = ContentScale.Crop,
                                            contentDescription = null
                                        )

                                        if (video.timestamp != null) {
                                            Timestamp(
                                                modifier = Modifier.align(Alignment.BottomEnd),
                                                text = video.timestamp
                                            )
                                        }
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
                                                text = video.subtitle,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        ChannelTab.Videos -> Unit
                        ChannelTab.Playlists -> Unit
                    }
                }
            }
            ChannelViewModel.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            is ChannelViewModel.State.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(36.dp),
                        imageVector = Icons.Default.Error,
                        contentDescription = stringResource(R.string.error)
                    )

                    Text(
                        text = stringResource(R.string.error_occurred),
                        style = MaterialTheme.typography.titleMedium
                    )

                    state.error.localizedMessage?.let {
                        SelectionContainer {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabHeader(
    selectedTab: ChannelTab,
    onClick: (ChannelTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        tabs = {
            ChannelTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    text = { Text(stringResource(tab.title)) },
                    icon = { Icon(tab.imageVector, null) },
                    onClick = { onClick(tab) }
                )
            }
        }
    )
}