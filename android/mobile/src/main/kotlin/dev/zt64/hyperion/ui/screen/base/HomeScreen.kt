package dev.zt64.hyperion.ui.screen.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import dev.zt64.hyperion.ui.component.VideoCard
import dev.zt64.hyperion.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = koinViewModel()
    val videos = viewModel.videos.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            LazyRow {

            }
        }

        items(
            count = videos.itemCount,
            key = videos.itemKey { it.id },
            contentType = videos.itemContentType()
        ) { index ->
            val video = videos[index] ?: return@items

            VideoCard(video)
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