package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.component.VideoCard
import dev.zt64.hyperion.ui.viewmodel.PlaylistViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PlaylistScreen(playlistId: String) {
    val viewModel: PlaylistViewModel = koinViewModel { parametersOf(playlistId) }

    when (val state = viewModel.state) {
        is PlaylistViewModel.State.Loading -> PlaylistLoadingScreen()
        is PlaylistViewModel.State.Loaded -> PlaylistLoadedScreen()
        is PlaylistViewModel.State.Error -> ErrorScreen(state.exception)
    }
}

@Composable
private fun PlaylistLoadedScreen() {
    val viewModel: PlaylistViewModel = koinViewModel()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val playlist = viewModel.playlist!!
    val snackbarHostState = remember { SnackbarHostState() }

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
                navigationIcon = { BackButton() },
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
                            contentDescription = stringResource(MR.strings.share)
                        )
                    }
                    IconButton(onClick = viewModel::download) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = stringResource(MR.strings.download)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
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
                        horizontalArrangement = Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterHorizontally
                        ),
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

                            Text(stringResource(MR.strings.play))
                        }

                        OutlinedButton(
                            modifier = Modifier.weight(1f, true),
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                            onClick = viewModel::shuffle
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = stringResource(MR.strings.shuffle)
                            )

                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                            Text(stringResource(MR.strings.shuffle))
                        }
                    }

                    Text(
                        text = playlist.videoCount,
                        style = MaterialTheme.typography.labelSmall
                    )
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
}

@Composable
private fun PlaylistLoadingScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
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