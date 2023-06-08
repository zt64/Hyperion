package com.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.domain.model.channel.imageVector
import com.hyperion.domain.model.channel.title
import com.hyperion.ui.component.ShimmerImage
import com.hyperion.ui.navigation.Destination
import com.hyperion.ui.viewmodel.ChannelViewModel
import com.zt.innertube.domain.model.DomainChannel
import com.zt.innertube.network.dto.browse.ChannelTab
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChannelScreen(
    channelId: String,
    viewModel: ChannelViewModel = koinViewModel(),
    navController: NavController<Destination>,
    onClickBack: () -> Unit
) = when (val state = viewModel.state) {
    is ChannelViewModel.State.Loaded -> {
        ChannelScreenLoaded(
            viewModel = viewModel,
            navController = navController,
            channel = state.channel
        )
    }

    ChannelViewModel.State.Loading -> {
        LaunchedEffect(Unit) {
            viewModel.getChannel(channelId)
        }

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

@Composable
private fun ChannelScreenLoaded(
    viewModel: ChannelViewModel,
    navController: NavController<Destination>,
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
                            url = banner.url,
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
                            url = channel.avatar,
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
                            enabled = viewModel.accountManager.loggedIn,
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
                    edgePadding = 0.dp
                ) {
                    val tabs = remember { ChannelTab.values() }

                    tabs.forEach { tab ->
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
                            onClick = { viewModel.getChannelTab(tab) }
                        )
                    }
                }
            }

            when (viewModel.tab) {
                ChannelTab.HOME -> {}
                ChannelTab.VIDEOS -> {}
                ChannelTab.SHORTS -> {}
                ChannelTab.LIVE -> {}
                ChannelTab.PLAYLISTS -> {}
                ChannelTab.COMMUNITY -> {}
                ChannelTab.STORE -> {}
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