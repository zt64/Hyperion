package com.hyperion.ui.screen.base

import androidx.compose.foundation.layout.Arrangement
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
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.hyperion.ui.component.VideoCard
import com.hyperion.ui.viewmodel.HomeViewModel
import com.hyperion.util.rememberLazyListState
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = getViewModel(),
    onClickVideo: (videoId: String) -> Unit,
    onClickChannel: (channelId: String) -> Unit
) {
    val videos = viewModel.videos.collectAsLazyPagingItems()
    val state = videos.rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        state = state,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            count = videos.itemCount,
            key = videos.itemKey { it.id },
            contentType = videos.itemContentType()
        ) { index ->
            val video = videos[index] ?: return@items

            VideoCard(
                video = video,
                onClick = { onClickVideo(video.id) },
                onClickChannel = { onClickChannel(video.channel!!.id) }
            )
        }

        item {
            videos.loadState.apply {
                when {
                    refresh is LoadState.Loading || append is LoadState.Loading -> {
                        CircularProgressIndicator()
                    }

                    append is LoadState.Error -> {
                        (append as LoadState.Error).error.message?.let {
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