package com.hyperion.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hyperion.R
import com.hyperion.ui.component.VideoCard
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.viewmodel.ChannelViewModel
import com.xinto.taxi.BackstackNavigator
import org.koin.androidx.compose.getViewModel

enum class ChannelTab(
    @StringRes
    val title: Int,
    val imageVector: ImageVector
) {
    HOME(R.string.home, Icons.Default.Home),
    VIDEOS(R.string.videos, Icons.Default.VideoLibrary),
    PLAYLISTS(R.string.playlists, Icons.Default.ViewList)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChannelScreen(
    viewModel: ChannelViewModel = getViewModel(),
    navigator: BackstackNavigator<AppDestination>,
    channelId: String
) {
    LaunchedEffect(Unit) {
        viewModel.getChannel(channelId)
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = viewModel.state) {
                is ChannelViewModel.State.Loaded -> {
                    var selectedTab by remember { mutableStateOf(ChannelTab.HOME) }

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
                                    contentScale = ContentScale.FillWidth,
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
                            ChannelTab.HOME -> {
                                items(channel.videos) { video ->
                                    VideoCard(
                                        modifier = Modifier.padding(horizontal = 14.dp),
                                        video = video,
                                        onClick = { navigator.push(AppDestination.Player(video.id)) }
                                    )
                                }
                            }
                            ChannelTab.VIDEOS -> Unit
                            ChannelTab.PLAYLISTS -> Unit
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
                            tint = MaterialTheme.colorScheme.error,
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