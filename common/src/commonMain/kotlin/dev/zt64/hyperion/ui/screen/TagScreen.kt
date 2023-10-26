package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import dev.zt64.hyperion.ui.component.LoadingIndicator
import dev.zt64.hyperion.ui.component.VideoCard
import dev.zt64.hyperion.ui.model.TagScreenModel
import dev.zt64.hyperion.ui.model.TagScreenModel.State
import org.koin.core.parameter.parametersOf

data class TagScreen(private val tag: String) : Screen {
    @Composable
    override fun Content() {
        val model = getScreenModel<TagScreenModel> { parametersOf(tag) }

        when (val state = model.state) {
            is State.Loading -> Loading()
            is State.Loaded -> Loaded()
            is State.Error -> ErrorScreenContent(state.exception)
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
    private fun Loaded() {
        val model = getScreenModel<TagScreenModel>()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AdaptiveTopBar(
                    title = { Text(model.tag!!) },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->
            val videos = model.videos.collectAsLazyPagingItems()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = model.subtitle!!,
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
                    LoadingIndicator(videos.loadState)
                }
            }
        }
    }
}