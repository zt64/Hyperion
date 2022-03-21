package com.hyperion.ui.screens

import android.icu.text.NumberFormat
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.hyperion.R
import com.hyperion.ui.components.player.Seekbar
import com.hyperion.ui.components.player.VideoActions
import com.hyperion.ui.screens.destinations.HomeScreenDestination
import com.hyperion.ui.viewmodel.PlayerViewModel
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchVideo(videoId)
    }

    if (viewModel.video != null) {
        viewModel.player.addMediaItem(MediaItem.fromUri(viewModel.video!!.formatStreams.last().url))
        viewModel.player.prepare()
    }

    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        Box {
            var showControls by remember { mutableStateOf(false) }
            val configuration = LocalConfiguration.current

            DisposableEffect(
                AndroidView(
                    modifier = Modifier
                        .wrapContentHeight()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { showControls = !showControls },
                                onDoubleTap = { offset ->
                                    if (offset.x > configuration.screenWidthDp / 2) viewModel.player.seekForward()
                                    else viewModel.player.seekBack()
                                }
                            )
                        },
                    factory = {
                        StyledPlayerView(context).apply {
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            useController = false
                            player = viewModel.player
                        }
                    }
                )
            ) {
                onDispose(viewModel.player::release)
            }

            PlayerControls(
                modifier = Modifier.matchParentSize(),
                visible = showControls,
                player = viewModel.player,
                isPlaying = viewModel.player.isPlaying,
                onMinimize = { navigator.navigate(HomeScreenDestination) },
                onPlayPause = viewModel::playPause
            )
        }

        if (viewModel.video != null) {
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
                            text = "${
                                NumberFormat.getInstance().format(viewModel.video!!.viewCount.toInt())
                            } ${stringResource(R.string.views)} - ${viewModel.video!!.publishedText}",
                            style = MaterialTheme.typography.labelMedium
                        )

                        VideoActions(
                            video = viewModel.video!!,
                            onLike = viewModel::like,
                            onDislike = viewModel::dislike,
                            onShare = viewModel::shareVideo,
                            onDownload = viewModel::download
                        )

                        Divider()

                        Row(
                            modifier = Modifier.clickable { viewModel.viewChannel() },
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(36.dp),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(viewModel.video!!.authorThumbnails.last().url)
                                    .crossfade(true)
                                    .build(),
                                placeholder = Icons.Default.AccountCircle.let { icon ->
                                    rememberVectorPainter(
                                        defaultWidth = icon.defaultWidth,
                                        defaultHeight = icon.defaultHeight,
                                        viewportWidth = icon.viewportWidth,
                                        viewportHeight = icon.viewportHeight,
                                        name = icon.name,
                                        tintColor = MaterialTheme.colorScheme.onSurface,
                                        tintBlendMode = icon.tintBlendMode,
                                        content = { _, _ -> RenderVectorGroup(icon.root) }
                                    )
                                },
                                contentDescription = null
                            )

                            Column {
                                Text(viewModel.video!!.author)
                                Text(
                                    text = "${viewModel.video!!.subCountText} subscribers",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f, true))

                            FilledTonalButton(
                                onClick = viewModel::subscribe
                            ) {
                                Text(stringResource(R.string.subscribe))
                            }
                        }

                        Divider()

                        Text(
                            modifier = Modifier
                                .clickable { expandedDescription = !expandedDescription }
                                .animateContentSize(animationSpec = tween()),
                            text = viewModel.video!!.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = if (expandedDescription) Int.MAX_VALUE else 5,
                            overflow = TextOverflow.Ellipsis
                        )

                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerControls(
    modifier: Modifier,
    visible: Boolean,
    player: Player,
    isPlaying: Boolean,
    onMinimize: () -> Unit,
    onPlayPause: () -> Unit
) = Box(
    modifier = modifier
) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.TopCenter),
        visible = visible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Row {
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
    }

    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row {
            IconButton(
                onClick = onPlayPause
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null
                )
            }
        }
    }

    AnimatedVisibility(
        modifier = Modifier.align(Alignment.BottomCenter),
        visible = visible,
        enter = slideInVertically { it / 2 } + fadeIn(),
        exit = slideOutVertically { it / 2 } + fadeOut()
    ) {
        Seekbar(
            modifier = Modifier.fillMaxWidth(),
            player = player
        )
    }
}