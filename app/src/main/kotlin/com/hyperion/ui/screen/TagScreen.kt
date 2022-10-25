package com.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.hyperion.R
import com.hyperion.ui.component.VideoCard
import com.hyperion.ui.viewmodel.TagViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun TagScreen(
    viewModel: TagViewModel = getViewModel(),
    tag: String,
    onClickVideo: (id: String) -> Unit,
    onClickBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getTag(tag)
    }

    when (val state = viewModel.state) {
        is TagViewModel.State.Error -> ErrorScreen(
            exception = state.exception,
            onClickBack = onClickBack
        )
        TagViewModel.State.Loading -> TagScreenLoading(
            onClickBack = onClickBack
        )
        TagViewModel.State.Loaded -> TagScreenLoaded(
            viewModel = viewModel,
            onClickVideo = onClickVideo,
            onClickBack = onClickBack
        )
    }
}

@Composable
private fun TagScreenLoading(
    onClickBack: () -> Unit
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
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
private fun TagScreenLoaded(
    viewModel: TagViewModel,
    onClickVideo: (id: String) -> Unit,
    onClickBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    Text(viewModel.tag!!)
                },
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.subtitle!!,
                        style = MaterialTheme.typography.titleMedium
                    )

                    LazyRow(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(viewModel.avatars) { avatarUrl ->
                            AsyncImage(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape),
                                model = avatarUrl,
                                contentDescription = null
                            )
                        }
                    }
                }
            }

            items(videos) { video ->
                if (video == null) return@items

                VideoCard(
                    video = video,
                    onClick = {
                        onClickVideo(video.id)
                    }
                )
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