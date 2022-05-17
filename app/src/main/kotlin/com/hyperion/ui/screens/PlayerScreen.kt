package com.hyperion.ui.screens

import android.text.format.DateUtils
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.hyperion.R
import com.hyperion.ui.components.ChannelThumbnail
import com.hyperion.ui.components.player.VideoActions
import com.hyperion.ui.screens.destinations.ChannelScreenDestination
import com.hyperion.ui.viewmodel.PlayerViewModel
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalFoundationApi::class)
@Destination(
    deepLinks = [
        DeepLink(uriPattern = "https://youtu.be/{videoId}"),
        DeepLink(uriPattern = "https://m.youtube.com/{videoId}"),
        DeepLink(uriPattern = "https://youtube.com/{videoId}"),
        DeepLink(uriPattern = "https://www.youtube.com/{videoId}")
    ]
)
@Composable
fun PlayerScreen(
    navigator: DestinationsNavigator,
    viewModel: PlayerViewModel = hiltViewModel(),
    videoId: String
) {
    LaunchedEffect(Unit) {
        viewModel.getVideo(videoId)
    }

    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        Box {
            var showControls by remember { mutableStateOf(false) }
            val configuration = LocalConfiguration.current

            AndroidView(
                modifier = Modifier
                    .aspectRatio(16f / 9f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { showControls = !showControls },
                            onDoubleTap = { offset ->
                                if (offset.x > configuration.screenWidthDp / 2) viewModel.player.seekForward()
                                else viewModel.player.seekBack()
                            }
                        )
                    },
                factory = { context ->
                    StyledPlayerView(context).apply {
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                        useController = true
                        player = viewModel.player
                    }
                }
            )

//            PlayerControls(
//                modifier = Modifier.matchParentSize(),
//                visible = showControls,
//                player = viewModel.player,
//                isPlaying = viewModel.player.isPlaying,
//                onMinimize = { navigator.navigate(HomeScreenDestination) },
//                onPlayPause = viewModel::playPause
//            )
        }

        when (viewModel.state) {
            PlayerViewModel.State.Loaded -> {
                // TODO: Move to view model in the future
                DisposableEffect(viewModel.video) {
                    val streams = viewModel.video!!.streams

                    val mediaItem = MediaItem.Builder()
                        .setUri(viewModel.video!!.streams.last().url)
                    viewModel.player.setMediaItem(MediaItem.fromUri(viewModel.video!!.streams.last().url))

//                    val factory = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
//                    val videoItem = factory.createMediaSource(MediaItem.fromUri(streams.filterIsInstance<DomainStream.Video>().first().url))
//                    val soundItem = factory.createMediaSource(MediaItem.fromUri(streams.filterIsInstance<DomainStream.Audio>().first().url))
//                    val mergedSource = MergingMediaSource(true, true, videoItem, soundItem)
//
//                    viewModel.player.setMediaSource(mergedSource)

                    viewModel.player.prepare()
                    viewModel.player.playWhenReady = true

                    onDispose(viewModel.player::release)
                }

                LazyColumn(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            var expandedDescription by remember { mutableStateOf(false) }

                            Text(
                                text = viewModel.video!!.title,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = viewModel.video!!.subtitle,
                                style = MaterialTheme.typography.labelMedium
                            )

                            VideoActions(
                                video = viewModel.video!!,
                                onLike = { },
                                onDislike = { },
                                onShare = viewModel::shareVideo,
                                onDownload = { }
                            )

                            Divider()

                            Row(
                                modifier = Modifier.clickable {
                                    navigator.navigate(ChannelScreenDestination(viewModel.video!!.author.id))
                                },
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChannelThumbnail(url = viewModel.video!!.author.avatarUrl!!)

                                Column(
                                    modifier = Modifier.weight(1f, true)
                                ) {
                                    Text(
                                        text = viewModel.video!!.author.name!!,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    viewModel.video!!.author.subscriberText?.let { subscriberText ->
                                        Text(
                                            text = subscriberText,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }

                                FilledTonalButton(
                                    onClick = viewModel::subscribe
                                ) {
                                    Text(stringResource(R.string.subscribe))
                                }
                            }

                            Divider()

                            if (viewModel.video!!.description.isNotBlank()) {
                                Text(
                                    modifier = Modifier
                                        .clickable { expandedDescription = !expandedDescription }
                                        .animateContentSize(animationSpec = tween()),
                                    text = viewModel.video!!.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = if (expandedDescription) Int.MAX_VALUE else 5,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Divider()
                        }
                    }

                    item {
                        Text(
                            text = "Comments",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    stickyHeader {
                        Text(
                            text = "Related videos",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            PlayerViewModel.State.Loading -> {}
            PlayerViewModel.State.Error -> {}
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlayerControls(
    modifier: Modifier,
    visible: Boolean,
    player: Player,
    isPlaying: Boolean,
    onMinimize: () -> Unit,
    onPlayPause: () -> Unit
) = AnimatedVisibility(
    modifier = modifier,
    visible = visible,
    enter = fadeIn(),
    exit = fadeOut()
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .animateEnterExit(
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                )
        ) {
            IconButton(
                onClick = onMinimize
            ) {
                Icon(Icons.Default.CloseFullscreen, null)
            }

            Spacer(modifier = Modifier.weight(1f, true))

            Box {
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(Icons.Default.MoreVert, null)
                }

                DropdownMenu(
                    expanded = false,
                    onDismissRequest = { /*TODO*/ }
                ) {
                    DropdownMenuItem(text = { Text("Speed") }, onClick = { /*TODO*/ })
                }
            }
        }

        Row(
            modifier = Modifier.align(Alignment.Center)
        ) {
            IconButton(
                onClick = onPlayPause
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .animateEnterExit(
                    enter = slideInVertically { it / 2 },
                    exit = slideOutVertically { it / 2 }
                ),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var position by remember { mutableStateOf(0L) }

            Text(
                text = buildString {
                    append(DateUtils.formatElapsedTime(player.currentPosition / 1000))
                    append(" / ")
                    append(DateUtils.formatElapsedTime(player.duration / 1000))
                },
                style = MaterialTheme.typography.labelMedium
            )

            Slider(
                modifier = Modifier.weight(1f, true),
                value = player.currentPosition.toFloat(),
                valueRange = if (player.duration == C.TIME_UNSET) 0f..0f else 0f..player.duration.toFloat(),
                onValueChange = { position = it.toLong() },
                onValueChangeFinished = { player.seekTo(position) }
            )

            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Fullscreen, contentDescription = "Fullscreen")
            }
        }
    }
}