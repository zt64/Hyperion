package com.hyperion.ui.screens

import android.icu.text.NumberFormat
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwipeDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.hyperion.R
import com.hyperion.model.Video
import com.hyperion.ui.components.ChannelThumbnail
import com.hyperion.ui.components.player.CommentCard
import com.hyperion.ui.components.player.Seekbar
import com.hyperion.ui.components.player.VideoActions
import com.hyperion.ui.screens.destinations.ChannelScreenDestination
import com.hyperion.util.InvidiousApi
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

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
    videoId: String
) {
    var video by remember { mutableStateOf<Video?>(null) }

    LaunchedEffect(Unit) {
        launch { video = InvidiousApi.getVideo(videoId) }
    }

    Column {
        if (video != null) {
            Box(
                modifier = Modifier.wrapContentSize()
            ) {
                val adaptiveFormat = remember { video!!.formatStreams.last() }
                val context = LocalContext.current
                val player = remember {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(adaptiveFormat.url))
                        prepare()

                        playWhenReady = true
                    }
                }
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
                                        if (offset.x > configuration.screenWidthDp / 2) player.seekBack()
                                        else player.seekForward()
                                    }
                                )
                            },
                        factory = {
                            StyledPlayerView(context).apply {
                                useController = false
                                this.player = player
                            }
                        }
                    )
                ) {
                    onDispose(player::release)
                }

                this@Column.AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    visible = showControls,
                    enter = expandVertically(expandFrom = Alignment.Top),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top)
                ) {
                    Row {
                        IconButton(
                            onClick = { /*TODO*/ }
                        ) {
                            Icon(Icons.Default.SwipeDown, null)
                        }

                        Spacer(modifier = Modifier.weight(1f, true))

                        IconButton(
                            onClick = { /*TODO*/ }
                        ) {
                            Icon(Icons.Default.MoreVert, null)
                        }
                    }
                }

                this@Column.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.Center),
                    visible = showControls,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (player.isPlaying) {

                        IconButton(
                            onClick = { player.pause() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { player.play() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                        }
                    }
                }

                this@Column.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    visible = showControls,
                    enter = expandVertically(expandFrom = Alignment.Bottom),
                    exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
                ) {
                    Seekbar(
                        modifier = Modifier.fillMaxWidth(),
                        player = player
                    )
                }
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
                            text = video!!.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "${
                                NumberFormat.getInstance().format(video!!.viewCount.toInt())
                            } ${stringResource(R.string.views)} - ${video!!.publishedText}",
                            style = MaterialTheme.typography.labelMedium
                        )

                        VideoActions(
                            video = video!!
                        )

                        Divider()

                        Row(
                            modifier = Modifier.clickable {
                                navigator.navigate(ChannelScreenDestination(video!!.authorUrl)) {

                                }
                            },
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ChannelThumbnail(authorId = video!!.authorId)

                            Column {
                                Text(video!!.author)
                                Text(
                                    text = "${video!!.subCountText} subscribers",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f, true))

                            FilledTonalButton(
                                modifier = Modifier.requiredWidth(IntrinsicSize.Min),
                                onClick = { /*TODO*/ }
                            ) {
                                Text(stringResource(R.string.subscribe))
                            }
                        }

                        Divider()

                        Text(
                            modifier = Modifier
                                .clickable { expandedDescription = !expandedDescription }
                                .animateContentSize(
                                    animationSpec = tween()
                                ),
                            text = video!!.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = if (expandedDescription) Int.MAX_VALUE else 5,
                            overflow = TextOverflow.Ellipsis
                        )

                        Divider()
                    }
                }

                items(5) { CommentCard() }
            }
        }
    }
}