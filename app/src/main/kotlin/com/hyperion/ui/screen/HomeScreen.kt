package com.hyperion.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hyperion.ui.component.VideoCard
import com.hyperion.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = getViewModel(),
    onClickVideo: (videoId: String) -> Unit,
    onClickChannel: (channelId: String) -> Unit
) {
    val videoListItems = viewModel.videos.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        state = lazyListState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(videoListItems) { video ->
            if (video == null) return@items

            VideoCard(
                video = video,
                onClick = { onClickVideo(video.id) },
                onClickChannel = { onClickChannel(video.author!!.id) }
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