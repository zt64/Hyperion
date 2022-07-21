package com.hyperion.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hyperion.ui.component.VideoCard
import com.hyperion.ui.screen.destinations.ChannelScreenDestination
import com.hyperion.ui.screen.destinations.PlayerScreenDestination
import com.hyperion.ui.viewmodel.HomeViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.koin.androidx.compose.getViewModel

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator = EmptyDestinationsNavigator,
    viewModel: HomeViewModel = getViewModel()
) {
    val videoListItems = viewModel.videos.collectAsLazyPagingItems()

    SwipeRefresh(
        state = rememberSwipeRefreshState(videoListItems.loadState.refresh == LoadState.Loading),
        onRefresh = videoListItems::refresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(videoListItems) { video ->
                if (video == null) return@items

                VideoCard(
                    video = video,
                    onClick = { navigator.navigate(PlayerScreenDestination(video.id)) },
                    onClickChannel = { navigator.navigate(ChannelScreenDestination(video.author!!.id)) }
                )
            }

            item {
                videoListItems.loadState.apply {
                    when (append) {
                        is LoadState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                        }
                        is LoadState.Error -> {
                            (append as LoadState.Error).error.printStackTrace()

                            Text("An error has occurred")
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}