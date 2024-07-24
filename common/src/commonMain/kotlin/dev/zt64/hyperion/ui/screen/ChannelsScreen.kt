package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import dev.zt64.hyperion.ui.component.ChannelCard
import dev.zt64.hyperion.ui.model.ChannelsScreenModel

object ChannelsScreen : Screen {
    @Composable
    override fun Content() {
        val model: ChannelsScreenModel = koinScreenModel()

        Scaffold(
            topBar = {
                AdaptiveTopBar(
                    title = { Text(stringResource(MR.strings.channels)) }
                )
            }
        ) { paddingValues ->
            val channels = model.channels.collectAsLazyPagingItems()
            val state = rememberLazyListState()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 14.dp),
                state = state,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    count = channels.itemCount,
                    key = channels.itemKey { it.id },
                    contentType = channels.itemContentType()
                ) { index ->
                    val channel = channels[index] ?: return@items

                    ChannelCard(
                        modifier = Modifier.animateItem(),
                        channel = channel,
                        onLongClick = { },
                        onClickSubscribe = { }
                    )
                }
            }
        }
    }
}