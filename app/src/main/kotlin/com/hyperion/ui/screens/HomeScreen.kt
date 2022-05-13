package com.hyperion.ui.screens

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hyperion.ui.components.VideoCard
import com.hyperion.ui.screens.destinations.ChannelScreenDestination
import com.hyperion.ui.screens.destinations.PlayerScreenDestination
import com.hyperion.ui.viewmodel.MainViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val videoListItems = viewModel.videos.collectAsLazyPagingItems()
    val refreshState = rememberSwipeRefreshState(videoListItems.loadState.refresh == LoadState.Loading)

    SwipeRefresh(
        state = refreshState,
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
                    onClick = { navController.navigate(PlayerScreenDestination(video.id)) },
                    onChannelClick = { navController.navigate(ChannelScreenDestination(video.author!!.id)) }
                )
            }

            videoListItems.loadState.apply {
                when (append) {
                    is LoadState.Loading -> {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                        }
                    }
                    is LoadState.Error -> {
                        item {
                            (append as LoadState.Error).error.printStackTrace()

                            Text("An error has occurred")
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}