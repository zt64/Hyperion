package com.hyperion.ui.screen

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.WindowInsetsController
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hyperion.R
import com.hyperion.domain.model.DomainStream
import com.hyperion.ui.component.*
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.viewmodel.PlayerViewModel
import com.hyperion.util.findActivity
import com.xinto.taxi.BackstackNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import kotlin.math.roundToInt

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = getViewModel(),
    navigator: BackstackNavigator<AppDestination>,
    videoId: String? = null
) {
    LaunchedEffect(Unit) {
        if (videoId != null) viewModel.loadVideo(videoId)
    }

    when (val state = viewModel.state) {
        is PlayerViewModel.State.Loading -> {
            PlayerScreenLoading()
        }
        is PlayerViewModel.State.Loaded -> {
            PlayerScreenLoaded(
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
private fun PlayerScreenLoading() {
    Column {
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

@Composable
private fun PlayerScreenLoaded(
    viewModel: PlayerViewModel,
    navigator: BackstackNavigator<AppDestination>
) {
    if (viewModel.showQualityPicker) {
        AlertDialog(
            onDismissRequest = viewModel::hideQualityPicker,
            icon = {
                Icon(
                    imageVector = Icons.Default.VideoSettings,
                    contentDescription = null
                )
            },
            title = { Text(stringResource(R.string.quality)) },
            text = {
                var selectedStream by remember { mutableStateOf(viewModel.stream!!) }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(viewModel.video!!.streams.filterIsInstance<DomainStream.Video>()) { stream ->
                        Row(
                            modifier = Modifier.clickable { selectedStream = stream },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                text = "${stream.label} ${stream.mimeType}",
                                style = MaterialTheme.typography.labelLarge
                            )

                            Spacer(Modifier.weight(1f, true))

                            RadioButton(
                                selected = stream == selectedStream,
                                onClick = { selectedStream = stream }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = viewModel::hideQualityPicker) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = viewModel::hideQualityPicker,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(stringResource(R.string.dismiss))
                }
            }
        )
    }
    if (viewModel.showDownloadDialog) {
        AlertDialog(
            onDismissRequest = viewModel::hideDownloadDialog,
            icon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(R.string.download)
                )
            },
            title = { Text(stringResource(R.string.download)) },
            text = {

            },
            confirmButton = {
                Button(onClick = viewModel::hideDownloadDialog) {
                    Text(stringResource(R.string.download))
                }
            },
            dismissButton = {
                Button(
                    onClick = viewModel::hideDownloadDialog,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(stringResource(R.string.dismiss))
                }
            }
        )
    }

    val context = LocalContext.current

    DisposableEffect(viewModel.isFullscreen) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose { }
        val insetsController = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        val originalOrientation = activity.requestedOrientation

        if (viewModel.isFullscreen) {
            insetsController.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }

        activity.requestedOrientation = if (viewModel.isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            originalOrientation
        }

        onDispose {
            activity.requestedOrientation = originalOrientation
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.player.release() }
    }

    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> PlayerScreenPortrait(viewModel = viewModel, navigator = navigator)
        Configuration.ORIENTATION_LANDSCAPE -> PlayerScreenLandscape(viewModel = viewModel, navigator = navigator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreenPortrait(
    viewModel: PlayerViewModel,
    navigator: BackstackNavigator<AppDestination>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val relatedVideos = viewModel.relatedVideos.collectAsLazyPagingItems()

        PlayerControls(
            viewModel = viewModel,
            navigator = navigator
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val video = viewModel.video!!

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
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "${video.viewCount} · ${video.uploadDate}",
                        style = MaterialTheme.typography.bodySmall
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

                    PlayerActions(
                        modifier = Modifier.fillMaxWidth(),
                        voteEnabled = false,
                        likeLabel = { Text(video.likesText) },
                        dislikeLabel = { Text(video.dislikesText) },
                        showDownloadButton = viewModel.preferences.showDownloadButton,
                        onClickVote = viewModel::updateVote,
                        onClickShare = viewModel::shareVideo,
                        onClickDownload = viewModel::showDownloadDialog
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
                                Text(
                                    text = video.author.name!!,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )

                                video.author.subscriberText?.let { subscriberText ->
                                    Text(
                                        text = subscriberText,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            FilledTonalButton(
                                enabled = false,
                                onClick = { }
                            ) {
                                Text(stringResource(R.string.subscribe))
                            }
                        }
                    }

                    Card(onClick = viewModel::showComments) {
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
                    onClickChannel = { navigator.push(AppDestination.Channel(relatedVideo.channel!!.id)) }
                )
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    relatedVideos.loadState.apply {
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
fun PlayerScreenLandscape(
    viewModel: PlayerViewModel,
    navigator: BackstackNavigator<AppDestination>
) {
    PlayerControls(
        modifier = Modifier.fillMaxHeight(),
        viewModel = viewModel,
        navigator = navigator
    )
}

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel,
    navigator: BackstackNavigator<AppDestination>
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }

    Box(
        modifier = modifier
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { viewModel.toggleControls() },
                    onDoubleTap = { offset ->
                        if (offset.x > size.width / 2) viewModel.skipForward() else viewModel.skipBackward()
                    }
                )
            }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { dragAmount ->
                    val offset = offsetY.value + dragAmount

                    if (viewModel.isFullscreen && offset in 0f..200f || !viewModel.isFullscreen && offset in -200f..0f) {
                        coroutineScope.launch {
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Player(player = viewModel.player)

        AnimatedVisibility(
            modifier = Modifier.matchParentSize(),
            visible = viewModel.showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                tonalElevation = 8.dp
            ) {
                PlayerControlsOverlay(
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
                    onClickMore = viewModel::showQualityPicker,
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
}