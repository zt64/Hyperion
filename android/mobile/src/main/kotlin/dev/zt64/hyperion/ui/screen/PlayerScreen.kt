package dev.zt64.hyperion.ui.screen

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.Player
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import dev.icerock.moko.resources.compose.stringResource
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.component.ShimmerImage
import dev.zt64.hyperion.ui.component.VideoCard
import dev.zt64.hyperion.ui.component.player.*
import dev.zt64.hyperion.ui.navigation.AppDestination
import dev.zt64.hyperion.ui.navigation.LocalNavController
import dev.zt64.hyperion.ui.sheet.CommentsSheet
import dev.zt64.hyperion.ui.sheet.DownloadSheet
import dev.zt64.hyperion.ui.sheet.PlayerSheet
import dev.zt64.hyperion.ui.viewmodel.PlayerViewModel
import dev.zt64.hyperion.util.findActivity
import dev.zt64.innertube.domain.model.DomainChapter
import dev.zt64.innertube.domain.model.DomainVideo
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

@Composable
fun PlayerScreen(videoId: String) {
    val viewModel: PlayerViewModel = koinViewModel { parametersOf(videoId) }

    when (val state = viewModel.state) {
        is PlayerViewModel.State.Loading -> PlayerScreenLoading()
        is PlayerViewModel.State.Loaded -> PlayerScreenLoaded()
        is PlayerViewModel.State.Error -> ErrorScreen(state.exception)
    }
}

@Composable
private fun PlayerScreenLoading() {
    Column {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .statusBarsPadding()
                .aspectRatio(WIDESCREEN_RATIO)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun PlayerScreenLoaded() {
    val viewModel: PlayerViewModel = koinViewModel()
    val context = LocalContext.current

    if (viewModel.showQualityPicker) {
        PlayerSheet(onDismissRequest = viewModel::hideOptions)
    }

    if (viewModel.showDownloadDialog) {
        DownloadSheet(onDismissRequest = viewModel::hideDownloadDialog)
    }

    if (viewModel.showCommentsSheet) {
        CommentsSheet(onDismissRequest = viewModel::hideComments)
    }

    DisposableEffect(viewModel.isFullscreen) {
        val activity = context.findActivity()
        val insetsController = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        val originalOrientation = activity.requestedOrientation

        if (viewModel.isFullscreen) {
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }

        activity.requestedOrientation = when {
            viewModel.isFullscreen -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> originalOrientation
        }

        onDispose {
            activity.requestedOrientation = originalOrientation
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    @SuppressLint("SwitchIntDef")
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> PlayerScreenPortrait()

        Configuration.ORIENTATION_LANDSCAPE -> PlayerControlsContainer(
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
private fun PlayerScreenPortrait() {
    val navController = LocalNavController.current
    val viewModel: PlayerViewModel = koinViewModel()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val relatedVideos = viewModel.relatedVideos.collectAsLazyPagingItems()

        PlayerControlsContainer()

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
                            items(
                                items = video.badges,
                                key = { it.hashCode() }
                            ) { badge ->
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

                    Description(
                        modifier = Modifier
                            .requiredHeightIn(max = if (viewModel.showFullDescription) Dp.Unspecified else 100.dp),
                        onClick = viewModel::toggleDescription,
                        video = video
                    )

                    PlayerActions(
                        modifier = Modifier.fillMaxWidth(),
                        voteEnabled = viewModel.accountManager.loggedIn,
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
                                url = video.author.avatarUrl!!,
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
                                enabled = viewModel.accountManager.loggedIn,
                                onClick = viewModel::toggleSubscription
                            ) {
                                Text(stringResource(MR.strings.subscribe))
                            }
                        }
                    }

                    Card(onClick = viewModel::showComments) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(MR.strings.comments),
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

            if (viewModel.preferences.showRelatedVideos) {
                items(
                    count = relatedVideos.itemCount,
                    key = relatedVideos.itemKey { it.id },
                    contentType = relatedVideos.itemContentType()
                ) { index ->
                    val relatedVideo = relatedVideos[index] ?: return@items

                    VideoCard(
                        video = relatedVideo,
                        onClick = { viewModel.loadVideo(relatedVideo.id) },
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
}

@Composable
private fun PlayerControlsContainer(
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current
    val viewModel: PlayerViewModel = koinViewModel()
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = modifier
            .background(Color.Black)
            .statusBarsPadding()
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { viewModel.toggleControls() },
                    onDoubleTap = { offset ->
                        if (offset.x > size.width / 2) {
                            viewModel.skipForward()
                        } else {
                            viewModel.skipBackward()
                        }
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
                    if (offsetY.value > 150 || offsetY.value < -150) viewModel.toggleFullscreen()

                    offsetY.animateTo(0f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Player(viewModel.player)

        AnimatedVisibility(
            modifier = Modifier
                .size(64.dp)
                .zIndex(99f),
            visible = viewModel.playbackState == Player.STATE_BUFFERING,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CircularProgressIndicator()
        }

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
                PlayerControls(
                    modifier = Modifier.matchParentSize(),
                    onClickCollapse = navController::pop
                )
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun Description(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    video: DomainVideo
) {
    val colorScheme = MaterialTheme.colorScheme
    val description = remember {
        buildAnnotatedString {
            append(video.description)

            val regex = """\b(?:https?://|www\.)\S+\b""".toRegex()

            regex.findAll(video.description).forEach { result ->
                addStyle(
                    style = SpanStyle(
                        color = colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    start = result.range.first,
                    end = result.range.last + 1
                )

                addUrlAnnotation(
                    urlAnnotation = UrlAnnotation(result.value),
                    start = result.range.first,
                    end = result.range.last + 1
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .animateContentSize()
            .then(modifier)
    ) {
        SelectionContainer {
            val uriHandler = LocalUriHandler.current

            ClickableText(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                overflow = TextOverflow.Ellipsis
            ) { offset ->
                val annotation = description.getUrlAnnotations(
                    start = offset,
                    end = offset
                ).firstOrNull()

                if (annotation == null) onClick() else uriHandler.openUri(annotation.item.url)
            }
        }

        // Chapters
        BoxWithConstraints {
            if (maxHeight >= 160.dp) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(
                        items = video.chapters,
                        key = DomainChapter::hashCode
                    ) { chapter ->
                        Chapter(
                            chapter = chapter,
                            onClick = { /* TODO */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Chapter(
    chapter: DomainChapter,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.size(
            width = 144.dp,
            height = 160.dp
        ),
        onClick = onClick
    ) {
        Column {
            ShimmerImage(
                modifier = Modifier.aspectRatio(WIDESCREEN_RATIO),
                url = chapter.thumbnail,
                contentDescription = null
            )

            Text(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 4.dp
                ),
                text = chapter.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}