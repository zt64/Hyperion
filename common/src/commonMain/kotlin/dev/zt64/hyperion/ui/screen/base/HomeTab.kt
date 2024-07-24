package dev.zt64.hyperion.ui.screen.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
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
        val windowSizeClass = LocalWindowSizeClass.current

        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                val state = rememberLazyListState()

                val pullToRefreshState = rememberPullToRefreshState()

                PullToRefreshBox(
                    isRefreshing = false,
                    onRefresh = {
                    },
                    state = pullToRefreshState
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            LazyRow {
                                // TODO
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

                        item(key = "loading") {
                            LoadingIndicator(videos.loadState)
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // item {
                    //     LazyRow {
                    //         // TODO
                    //     }
                    // }

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
                }
            }
        }
    }
}