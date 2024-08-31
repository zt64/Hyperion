package dev.zt64.hyperion.ui.screen.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.component.LoadingIndicator
import dev.zt64.hyperion.ui.component.VideoCard
import dev.zt64.hyperion.ui.layout.PullToRefreshBox
import dev.zt64.hyperion.ui.model.HomeScreenModel

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(MR.strings.home)
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val model: HomeScreenModel = koinScreenModel()
        val videos = model.videos.collectAsLazyPagingItems()
        val pullToRefreshState = rememberPullToRefreshState()

        // TODO: Disable PTR on Desktop somehow
        PullToRefreshBox(
            isRefreshing = videos.loadState.refresh is LoadState.Loading,
            onRefresh = videos::refresh,
            state = pullToRefreshState
        ) {
            val windowSizeClass = LocalWindowSizeClass.current

            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    val state = rememberLazyListState()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (videos.itemCount > 0) {
                            item {
                                FilterRow(
                                    filters = model.filters,
                                    onClickFilter = { filter ->
                                    }
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

                            item(key = "loading") {
                                LoadingIndicator(videos.loadState)
                            }
                        } else {
                            item {
                                NoVideos()
                            }
                        }
                    }
                }
                WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
                    val state = rememberLazyGridState()

                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Adaptive(320.dp),
                        state = state,
                        horizontalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (videos.itemCount > 0) {
                            item(
                                span = {
                                    GridItemSpan(maxLineSpan)
                                }
                            ) {
                                FilterRow(
                                    filters = model.filters,
                                    onClickFilter = { filter ->
                                    }
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

                            item(
                                key = "loading",
                                span = {
                                    GridItemSpan(maxLineSpan)
                                }
                            ) {
                                LoadingIndicator(videos.loadState)
                            }
                        } else {
                            item(
                                span = {
                                    GridItemSpan(maxLineSpan)
                                }
                            ) {
                                NoVideos()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(filters: List<String>, onClickFilter: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = false,
                onClick = {
                    onClickFilter(filter)
                },
                label = {
                    Text(filter)
                }
            )
        }
    }
}

/**
 * Composable that displays a message when there are no videos to display
 *
 */
@Composable
private fun NoVideos() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        OutlinedCard {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Try searching to get recommendations",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}