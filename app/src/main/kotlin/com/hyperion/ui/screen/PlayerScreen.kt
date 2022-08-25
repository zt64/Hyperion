package com.hyperion.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.component.*
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.viewmodel.PlayerViewModel
import com.xinto.taxi.BackstackNavigator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import kotlin.math.roundToInt

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = getViewModel(),
    navigator: BackstackNavigator<AppDestination>,
    videoId: String? = null
) {
    val state = viewModel.state

    LaunchedEffect(Unit) {
        if (videoId != null) viewModel.loadVideo(videoId)
    }

    when (state) {
        is PlayerViewModel.State.Loading -> {
            PlayerScreenLoading(
                modifier = Modifier.fillMaxSize()
            )
        }
        is PlayerViewModel.State.Loaded -> {
            PlayerScreenLoaded(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                navigator = navigator
            )
        }
        is PlayerViewModel.State.Error -> {
            PlayerScreenError(
                exception = state.exception,
                onClickBack = navigator::pop
            )
        }
    }
}

@Composable
private fun PlayerScreenLoading(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .aspectRatio(16f / 9f)
                .fillMaxSize()
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerScreenLoaded(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel,
    navigator: BackstackNavigator<AppDestination>,
    prefs: PreferencesManager = get()
) {
    Column(
        modifier = modifier
    ) {
        val coroutineScope = rememberCoroutineScope()
        val offsetY = remember { Animatable(0f) }

        Box(
            modifier = Modifier
                .wrapContentSize()
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .pointerInput(Unit) {
                    coroutineScope {
                        launch {
                            detectTapGestures(
                                onTap = { viewModel.toggleControls() },
                                onDoubleTap = { offset ->
                                    if (offset.x > size.width / 2) viewModel.skipForward() else viewModel.skipBackward()
                                }
                            )
                        }
                    }
                }
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { dragAmount ->
                        coroutineScope.launch {
                            val offset = offsetY.value + dragAmount

                            if (viewModel.isFullscreen && offset in 0f..200f || !viewModel.isFullscreen && offset in -200f..0f) {
                                offsetY.snapTo(offset)
                            }
                        }
                    },
                    onDragStopped = {
                        when {
                            offsetY.value > 150 -> viewModel.exitFullscreen()
                            offsetY.value < -150 -> viewModel.enterFullscreen()
                        }


                        offsetY.animateTo(0f)
                    }
                )
        ) {
            DisposableEffect(Unit) {
                onDispose { viewModel.player.pause() }
            }

            Player(player = viewModel.player)

            this@Column.AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = viewModel.showControls,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    tonalElevation = 8.dp
                ) {
                    PlayerControls(
                        modifier = Modifier.matchParentSize(),
                        isFullscreen = viewModel.isFullscreen,
                        isPlaying = viewModel.isPlaying,
                        position = viewModel.position,
                        duration = viewModel.duration,
                        onSkipNext = viewModel::skipNext,
                        onSkipPrevious = viewModel::skipPrevious,
                        onClickCollapse = navigator::pop,
                        onClickPlayPause = viewModel::togglePlayPause,
                        onClickFullscreen = viewModel::toggleFullscreen,
                        onClickMore = viewModel::toggleMoreOptions,
                        onSeek = viewModel::seekTo
                    )
                }
            }

//            SeekBar(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.BottomStart),
//                duration = Duration.ZERO,
//                value = viewModel.position.inWholeMilliseconds.toFloat(),
//                valueRange = 0f..viewModel.duration.inWholeMilliseconds.toFloat(),
//                onSeek = viewModel::seekTo,
//                onSeekFinished = { }
//            )
        }

        val video = viewModel.video!!
        val relatedVideos = viewModel.relatedVideos.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier.padding(horizontal = 14.dp).clickable {
                viewModel.toggleDescription()
            },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val uriHandler = LocalUriHandler.current

                    // TODO: Add automatic hyperlinks
                    val description = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            append(video.description)
                        }
                    }

                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,

                    )

                    Text(
                        text = "${video.viewCount} · ${video.uploadDate}",
                        style = MaterialTheme.typography.bodySmall,

                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(video.badges) { badge ->
                            SuggestionChip(
                                modifier = Modifier.height(26.dp),
                                label = { Text(badge) },
                                onClick = { /* TODO: Redirect to badge page */ }
                            )
                        }
                    }

                    SelectionContainer {
                        ClickableText(
                            modifier = Modifier.animateContentSize(),
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = if (viewModel.showFullDescription) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis
                        ) { offset ->
                            val annotation = description.getStringAnnotations(
                                tag = "URL",
                                start = offset,
                                end = offset
                            ).firstOrNull()

                            if (annotation == null) viewModel.toggleDescription() else uriHandler.openUri(annotation.item)
                        }
                    }

                    VideoActions(
                        modifier = Modifier.fillMaxWidth(),
                        voteEnabled = false,
                        likeLabel = { Text(video.likesText) },
                        dislikeLabel = { Text(video.dislikesText) },
                        showDownloadButton = prefs.showDownloadButton,
                        onClickVote = viewModel::updateVote,
                        onClickShare = viewModel::shareVideo,
                        onClickDownload = viewModel::download
                    )

                    Card(
                        onClick = { navigator.push(AppDestination.Channel(video.author.id)) }
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ChannelThumbnail(
                                modifier = Modifier.size(42.dp),
                                url = video.author.avatarUrl!!
                            )

                            Column(
                                modifier = Modifier.weight(1f, true)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = video.author.name!!,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    if (video.author.verified) {
                                        Icon(
                                            modifier = Modifier.size(16.dp),
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = stringResource(R.string.verified)
                                        )
                                    }
                                }

                                video.author.subscriberText?.let { subscriberText ->
                                    Text(
                                        text = subscriberText,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            FilledTonalToggleButton(
                                enabled = false,
                                checked = false,
                                onCheckedChange = viewModel::updateSubscription
                            ) {
                                Text(stringResource(R.string.subscribe))
                            }
                        }
                    }

                    Card(
                        onClick = viewModel::showComments
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.comments),
                                style = MaterialTheme.typography.titleSmall
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null
                                )
                                Text(
                                    text = "Some top comment here",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            items(relatedVideos) { relatedVideo ->
                if (relatedVideo == null) return@items

                VideoCard(
                    video = relatedVideo,
                    onClick = { viewModel.loadVideo(relatedVideo.id) },
                    onClickChannel = { navigator.push(AppDestination.Channel(relatedVideo.author!!.id)) }
                )
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    relatedVideos.loadState.apply {
                        when (append) {
                            is LoadState.NotLoading,
                            is LoadState.Loading -> {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                            is LoadState.Error -> {
                                (append as LoadState.Error).error.message?.let { error ->
                                    Text(
                                        text = error,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerScreenError(
    exception: Exception,
    onClickBack: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
}