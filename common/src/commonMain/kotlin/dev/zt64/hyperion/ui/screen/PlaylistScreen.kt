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
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.component.*
import dev.zt64.hyperion.ui.model.PlaylistScreenModel
import dev.zt64.hyperion.ui.model.PlaylistScreenModel.State
import org.koin.core.parameter.parametersOf

data class PlaylistScreen(val id: String) : Screen {
    @Composable
    override fun Content() {
        val model: PlaylistScreenModel = getScreenModel { parametersOf(id) }

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
        val model: PlaylistScreenModel = getScreenModel()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val playlist = model.playlist!!
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AdaptiveTopBar(
                    title = {
                        Text(
                            modifier = Modifier.basicMarquee(),
                            text = playlist.name,
                            softWrap = false
                        )
                    },
                    actions = {
                        IconButton(onClick = model::saveToLibrary) {
                            Icon(
                                imageVector = Icons.Default.LibraryAdd,
                                contentDescription = null
                            )
                        }
                        ShareButton(playlist.shareUrl, playlist.name)
                        IconButton(onClick = model::download) {
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
                    snackbar = { data -> Snackbar(data) }
                )
            }
        ) { paddingValues ->
            val videos = model.videos.collectAsLazyPagingItems()

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
                                space = 12.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                modifier = Modifier.weight(1f, true),
                                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                                onClick = model::play
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
                                onClick = model::shuffle
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
                    LoadingIndicator(videos.loadState)
                }
            }
        }
    }
}