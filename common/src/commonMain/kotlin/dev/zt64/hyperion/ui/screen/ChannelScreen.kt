package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.pluralStringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.api.domain.model.DomainChannel
import dev.zt64.hyperion.api.network.dto.browse.ChannelTab
import dev.zt64.hyperion.domain.model.channel.imageVector
import dev.zt64.hyperion.domain.model.channel.title
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import dev.zt64.hyperion.ui.component.ShareIconButton
import dev.zt64.hyperion.ui.component.ShimmerImage
import dev.zt64.hyperion.ui.model.ChannelScreenModel
import dev.zt64.hyperion.ui.model.ChannelScreenModel.State
import org.koin.core.parameter.parametersOf

data class ChannelScreen(private val id: String) : Screen {
    @Composable
    override fun Content() {
        val model: ChannelScreenModel = koinScreenModel { parametersOf(id) }

        when (val state = model.state) {
            is State.Loaded -> Loaded(state.channel)
            is State.Loading -> Loading()
            is State.Error -> ErrorScreenContent(state.error)
        }
    }

    @Composable
    private fun Loading() {
        Scaffold(
            topBar = { AdaptiveTopBar() }
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
    private fun Loaded(channel: DomainChannel) {
        val model: ChannelScreenModel = koinScreenModel()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AdaptiveTopBar(
                    title = { Text(channel.name) },
                    actions = { ShareIconButton(channel.shareUrl, label = channel.name) },
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
                                modifier = Modifier.aspectRatio(
                                    banner.defaultBanner.width.toFloat() / banner.defaultBanner.height.toFloat()
                                ),
                                url = banner.defaultBanner.url,
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
                                        text = pluralStringResource(MR.plurals.subscribers, 1000, 1000),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            FilledTonalButton(
                                enabled = model.accountManager.loggedIn,
                                onClick = model::subscribe
                            ) {
                                Text(stringResource(MR.strings.subscribe))
                            }
                        }
                    }
                }

                stickyHeader {
                    PrimaryScrollableTabRow(
                        modifier = Modifier.fillMaxWidth(),
                        selectedTabIndex = model.tab.ordinal,
                        edgePadding = 0.dp
                    ) {
                        ChannelTab.entries.forEach { tab ->
                            LeadingIconTab(
                                selected = model.tab == tab,
                                icon = {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = tab.imageVector,
                                        contentDescription = null
                                    )
                                },
                                text = { Text(stringResource(tab.title)) },
                                onClick = { model.getChannelTab(tab) }
                            )
                        }
                    }
                }

                when (model.tab) {
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
    private fun AboutTab() {
    }
}