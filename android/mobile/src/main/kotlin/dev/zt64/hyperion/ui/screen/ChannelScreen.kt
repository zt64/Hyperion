package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.domain.model.channel.imageVector
import dev.zt64.hyperion.domain.model.channel.title
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.component.ShimmerImage
import dev.zt64.hyperion.ui.viewmodel.ChannelViewModel
import dev.zt64.innertube.domain.model.DomainChannel
import dev.zt64.innertube.network.dto.browse.ChannelTab
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChannelScreen(channelId: String) {
    val viewModel: ChannelViewModel = koinViewModel { parametersOf(channelId) }

    when (val state = viewModel.state) {
        is ChannelViewModel.State.Loaded -> ChannelScreenLoaded(state.channel)
        is ChannelViewModel.State.Loading -> ChannelScreenLoading()
        is ChannelViewModel.State.Error -> ErrorScreen(state.error)
    }
}

@Composable
private fun ChannelScreenLoaded(
    channel: DomainChannel
) {
    val viewModel: ChannelViewModel = koinViewModel()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(channel.name) },
                navigationIcon = { BackButton() },
                actions = {
                    IconButton(onClick = viewModel::shareChannel) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(MR.strings.share)
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
                            Text(stringResource(MR.strings.subscribe))
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
                    ChannelTab.entries.forEach { tab ->
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
private fun ChannelScreenLoading() {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton() },
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