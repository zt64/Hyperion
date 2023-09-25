package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.component.VideoCard
import dev.zt64.hyperion.ui.viewmodel.TagViewModel
import dev.zt64.hyperion.ui.viewmodel.TagViewModel.State
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TagScreen(tag: String) {
    val viewModel: TagViewModel = koinViewModel { parametersOf(tag) }

    when (val state = viewModel.state) {
        is State.Loading -> TagScreenLoading()
        is State.Loaded -> TagScreenLoaded()
        is State.Error -> ErrorScreen(state.exception)
    }
}

@Composable
private fun TagScreenLoading() {
    Scaffold(
        topBar = {
            MediumTopAppBar(
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

@Composable
private fun TagScreenLoaded() {
    val viewModel: TagViewModel = koinViewModel()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                navigationIcon = { BackButton() },
                title = { Text(viewModel.tag!!) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        val videos = viewModel.videos.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = viewModel.subtitle!!,
                    style = MaterialTheme.typography.titleMedium
                )
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
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
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
    }
}