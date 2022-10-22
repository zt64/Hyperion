package com.hyperion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
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
                onClickBack = onClickBack
            )
        }

        PlaylistViewModel.State.Loading -> {
            PlaylistLoadingScreen(
                onClickBack = onClickBack
            )
        }

        is PlaylistViewModel.State.Error -> {
            PlaylistErrorScreen(
                exception = state.exception,
                onClickBack = onClickBack
            )
        }
    }
}

@Composable
private fun PlaylistLoadedScreen(
    viewModel: PlaylistViewModel,
    onClickBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val playlist = viewModel.playlist!!

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(playlist.name) },
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            onClick = {

                            }
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
                            onClick = {

                            }
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

            items(videos) {video ->
                if (video == null) return@items

                VideoCard(
                    video = video,
                    onClick = { /*TODO*/ }
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

@Composable
private fun PlaylistErrorScreen(
    exception: Exception,
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
                title = { Text(stringResource(R.string.error)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(36.dp),
                imageVector = Icons.Default.Error,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = stringResource(R.string.error)
            )

            Text(
                text = stringResource(R.string.error_occurred),
                style = MaterialTheme.typography.titleMedium
            )

            exception.localizedMessage?.let { message ->
                SelectionContainer {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}