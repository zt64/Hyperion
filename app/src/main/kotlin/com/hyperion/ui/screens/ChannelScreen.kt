package com.hyperion.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hyperion.R
import com.hyperion.ui.components.VideoCard
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

@OptIn(ExperimentalFoundationApi::class)
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

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val channel = state.channel

                    item {
                        Column {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .clip(MaterialTheme.shapes.medium),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(channel.banner)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null
                            )

                            Row(
                                modifier = Modifier.padding(top = 14.dp, start = 8.dp, end = 8.dp),
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
                                VideoCard(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    video = video,
                                    onClick = { navigator.navigate(PlayerScreenDestination(video.id)) }
                                )
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
private fun TabHeader(
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