package com.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.domain.model.channel.ChannelTab
import com.hyperion.domain.model.channel.VideoSort
import com.hyperion.ui.component.ShimmerImage
import com.hyperion.ui.component.VideoCard
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.viewmodel.ChannelViewModel
import com.zt.innertube.domain.model.DomainChannel
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import org.koin.androidx.compose.getViewModel

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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
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
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    channel.banner?.let { banner ->
                        ShimmerImage(
                            modifier = Modifier
                                .aspectRatio(banner.width.toFloat() / banner.height.toFloat()),
                            model = banner.url,
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
                ScrollableTabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = viewModel.tab.ordinal,
                    edgePadding = 0.dp,
                    tabs = {
                        ChannelTab.values().forEach { tab ->
                            LeadingIconTab(
                                selected = viewModel.tab == tab,
                                icon = {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = tab.imageVector,
                                        contentDescription = null
                                    )
                                },
                                text = { Text(stringResource(tab.title)) },
                                onClick = { viewModel.tab = tab }
                            )
                        }
                    }
                )
            }

            when (viewModel.tab) {
                ChannelTab.HOME -> {

                }

                ChannelTab.VIDEOS -> {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(VideoSort.values()) { sort ->
                                FilterChip(
                                    selected = viewModel.videoSort == sort,
                                    onClick = { viewModel.videoSort = sort },
                                    label = { Text(stringResource(sort.displayName)) }
                                )
                            }
                        }
                    }

                    items(channel.items) { video ->
                        VideoCard(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            video = video,
                            onClick = { navController.navigate(AppDestination.Player(video.id)) }
                        )
                    }
                }

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