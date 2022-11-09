package com.hyperion.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.ui.component.ShimmerImage
import com.hyperion.ui.component.VideoCard
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.viewmodel.ChannelViewModel
import com.zt.innertube.domain.model.DomainChannel
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import org.koin.androidx.compose.getViewModel

private enum class ChannelTab(
    @StringRes
    val title: Int,
    val imageVector: ImageVector
) {
    HOME(R.string.home, Icons.Default.Home),
    VIDEOS(R.string.videos, Icons.Default.VideoLibrary),
    PLAYLISTS(R.string.playlists, Icons.Default.ViewList),
    COMMUNITY(R.string.community, Icons.Default.People),
    CHANNELS(R.string.channels, Icons.Default.AccountTree),
    ABOUT(R.string.about, Icons.Default.Info)
}

@Composable
fun ChannelScreen(
    viewModel: ChannelViewModel = getViewModel(),
    navController: NavController<AppDestination>,
    channelId: String,
    onClickBack: () -> Unit
) {
    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.getChannel(channelId)
    }

    when (state) {
        is ChannelViewModel.State.Loaded -> {
            ChannelScreenLoaded(
                viewModel = viewModel,
                navController = navController,
                channel = state.channel
            )
        }

        ChannelViewModel.State.Loading -> {
            ChannelScreenLoading(
                onClickBack = onClickBack
            )
        }

        is ChannelViewModel.State.Error -> {
            ErrorScreen(
                exception = state.error,
                onClickBack = onClickBack
            )
        }
    }
}

@Composable
private fun ChannelScreenLoaded(
    viewModel: ChannelViewModel,
    navController: NavController<AppDestination>,
    channel: DomainChannel
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(channel.name) },
                navigationIcon = {
                    IconButton(onClick = navController::pop) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::shareChannel) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        var selectedTab by rememberSaveable { mutableStateOf(ChannelTab.HOME) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (channel.banner != null) {
                        ShimmerImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .clip(MaterialTheme.shapes.medium),
                            model = channel.banner,
                            contentDescription = channel.name
                        )
                    }

                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ShimmerImage(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(52.dp),
                            model = channel.avatar,
                            contentDescription = channel.name
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
                            enabled = false,
                            onClick = viewModel::subscribe
                        ) {
                            Text(stringResource(R.string.subscribe))
                        }
                    }
                }
            }

            stickyHeader {
                TabHeader(
                    selectedTab = selectedTab,
                    onClickTab = { selectedTab = it }
                )
            }

            when (selectedTab) {
                ChannelTab.HOME -> {
                    items(channel.items) { video ->
                        VideoCard(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            video = video,
                            onClick = { navController.navigate(AppDestination.Player(video.id)) }
                        )
                    }
                }

                ChannelTab.VIDEOS -> {}
                ChannelTab.PLAYLISTS -> {}
                ChannelTab.COMMUNITY -> {}
                ChannelTab.CHANNELS -> {}
                ChannelTab.ABOUT -> {}
            }
        }
    }
}

@Composable
private fun ChannelScreenLoading(
    onClickBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = { }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun TabHeader(
    selectedTab: ChannelTab,
    onClickTab: (ChannelTab) -> Unit
) {
    ScrollableTabRow(
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = selectedTab.ordinal,
        edgePadding = 0.dp,
        tabs = {
            ChannelTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    text = { Text(stringResource(tab.title)) },
                    icon = { Icon(tab.imageVector, null) },
                    onClick = { onClickTab(tab) }
                )
            }
        }
    )
}