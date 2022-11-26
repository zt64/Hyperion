package com.hyperion.ui.screen

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.hyperion.ui.component.*
import com.hyperion.ui.component.player.Player
import com.hyperion.ui.component.player.PlayerActions
import com.hyperion.ui.component.player.PlayerControlsOverlay
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.viewmodel.PlayerViewModel
import com.hyperion.util.findActivity
import com.zt.innertube.domain.model.DomainStream
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import kotlin.math.roundToInt

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = getViewModel(),
    navController: NavController<AppDestination>,
    videoId: String? = null
) {
    LaunchedEffect(Unit) {
        if (viewModel.video == null && videoId != null) viewModel.loadVideo(videoId)
    }

    when (val state = viewModel.state) {
        is PlayerViewModel.State.Loading -> PlayerScreenLoading()

        is PlayerViewModel.State.Loaded -> {
            PlayerScreenLoaded(
                viewModel = viewModel,
                navController = navController
            )
        }

        is PlayerViewModel.State.Error -> {
            ErrorScreen(
                exception = state.exception,
                onClickBack = navController::pop
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
                .statusBarsPadding()
                .aspectRatio(16f / 9f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun PlayerScreenLoaded(
    viewModel: PlayerViewModel,
    navController: NavController<AppDestination>
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStream = stream },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = stream == selectedStream,
                                onClick = { selectedStream = stream }
                            )

                            Text(
                                text = "${stream.label} ${stream.mimeType}",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::hideQualityPicker) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideQualityPicker) {
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
                TextButton(onClick = viewModel::hideDownloadDialog) {
                    Text(stringResource(R.string.download))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideDownloadDialog) {
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
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

    @SuppressLint("SwitchIntDef")
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> PlayerScreenPortrait(
            viewModel = viewModel,
            navController = navController
        )

        Configuration.ORIENTATION_LANDSCAPE -> PlayerScreenLandscape(
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
private fun PlayerScreenPortrait(
    viewModel: PlayerViewModel,
    navController: NavController<AppDestination>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val relatedVideos = viewModel.relatedVideos.collectAsLazyPagingItems()

        PlayerControls(
            viewModel = viewModel,
            navController = navController
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
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

                    if (video.badges.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(video.badges) { badge ->
                                SuggestionChip(
                                    modifier = Modifier.height(26.dp),
                                    label = { Text(badge) },
                                    onClick = {
                                        navController.navigate(AppDestination.Tag(badge))
                                    }
                                )
                            }
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
                        onClick = { navController.navigate(AppDestination.Channel(video.author.id)) }
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ShimmerImage(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(42.dp),
                                model = video.author.avatarUrl!!,
                                contentDescription = video.author.name!!
                            )

                            Column(
                                modifier = Modifier.weight(1f, true)
                            ) {
                                Text(
                                    text = video.author.name!!,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )

                                video.author.subscriptionsText?.let { subscriptionsText ->
                                    Text(
                                        text = subscriptionsText,
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
                    onClickChannel = { navController.navigate(AppDestination.Channel(relatedVideo.channel!!.id)) }
                )
            }

            item {
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

@Composable
private fun PlayerScreenLandscape(
    viewModel: PlayerViewModel,
    navController: NavController<AppDestination>
) {
    PlayerControls(
        modifier = Modifier.fillMaxHeight(),
        viewModel = viewModel,
        navController = navController
    )
}

@Composable
private fun PlayerControls(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel,
    navController: NavController<AppDestination>
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .statusBarsPadding()
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { viewModel.toggleControls() },
                    onDoubleTap = { offset ->
                        if (offset.x > size.width / 2) viewModel.skipForward() else viewModel.skipBackward()
                    }
                )
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(transformableState)
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
                    onClickCollapse = navController::pop,
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