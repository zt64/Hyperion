package com.hyperion.ui.screen

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hyperion.R
import com.hyperion.ui.component.VideoCard
import com.hyperion.ui.viewmodel.PlaylistViewModel
import com.hyperion.util.rememberLazyListState
import org.koin.androidx.compose.getViewModel

@Composable
fun PlaylistScreen(
    viewModel: PlaylistViewModel = getViewModel(),
    playlistId: String,
    onClickVideo: (id: String) -> Unit,
    onClickBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getPlaylist(playlistId)
    }

    when (val state = viewModel.state) {
        is PlaylistViewModel.State.Loaded -> {
            PlaylistLoadedScreen(
                viewModel = viewModel,
                onClickVideo = onClickVideo,
                onClickBack = onClickBack
            )
        }

        PlaylistViewModel.State.Loading -> {
            PlaylistLoadingScreen(
                onClickBack = onClickBack
            )
        }

        is PlaylistViewModel.State.Error -> {
            ErrorScreen(
                exception = state.exception,
                onClickBack = onClickBack
            )
        }
    }
}

@Composable
private fun PlaylistLoadedScreen(
    viewModel: PlaylistViewModel,
    onClickVideo: (id: String) -> Unit,
    onClickBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val playlist = viewModel.playlist!!

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.basicMarquee(),
                        text = playlist.name,
                        softWrap = false
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::saveToLibrary) {
                        Icon(
                            imageVector = Icons.Default.LibraryAdd,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = viewModel::sharePlaylist) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share)
                        )
                    }
                    IconButton(onClick = viewModel::download) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = stringResource(R.string.download)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = viewModel.snackbarHostState,
                snackbar = { data ->
                    Snackbar(data)
                }
            )
        }
    ) { paddingValues ->
        val videos = viewModel.videos.collectAsLazyPagingItems()
        val state = videos.rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = playlist.channel.name!!,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            modifier = Modifier.weight(1f, true),
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                            onClick = viewModel::play
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null
                            )

                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                            Text(stringResource(R.string.play))
                        }

                        OutlinedButton(
                            modifier = Modifier.weight(1f, true),
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                            onClick = viewModel::shuffle
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = stringResource(R.string.shuffle)
                            )

                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                            Text(stringResource(R.string.shuffle))
                        }
                    }

                    Text(
                        text = playlist.videoCount,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            items(
                items = videos,
                key = { it.id }
            ) {video ->
                if (video == null) return@items

                VideoCard(
                    video = video,
                    onClick = { onClickVideo(video.id) }
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
}

@Composable
private fun PlaylistLoadingScreen(
    onClickBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
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