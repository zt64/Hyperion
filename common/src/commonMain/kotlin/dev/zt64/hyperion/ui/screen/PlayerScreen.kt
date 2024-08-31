package dev.zt64.hyperion.ui.screen

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
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.api.domain.model.DomainChapter
import dev.zt64.hyperion.api.domain.model.DomainVideo
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.component.LoadingIndicator
import dev.zt64.hyperion.ui.component.ShimmerImage
import dev.zt64.hyperion.ui.component.VideoCard
import dev.zt64.hyperion.ui.component.player.PlayerActions
import dev.zt64.hyperion.ui.component.player.PlayerOverlay
import dev.zt64.hyperion.ui.component.player.WIDESCREEN_RATIO
import dev.zt64.hyperion.ui.form.DownloadForm
import dev.zt64.hyperion.ui.menu.PlayerOptionsMenu
import dev.zt64.hyperion.ui.model.PlaybackState
import dev.zt64.hyperion.ui.model.PlayerScreenModel
import dev.zt64.hyperion.ui.model.PlayerState
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

class PlayerScreen(private val id: String) : Screen {
    @Composable
    override fun Content() {
        val model: PlayerScreenModel = koinScreenModel {
            parametersOf(id)
        }

        when (val state = model.state) {
            is PlayerState.Loading -> Loading()
            is PlayerState.Loaded -> Loaded()
            is PlayerState.Error -> ErrorScreenContent(state.exception)
        }
    }

    @Composable
    private fun Loading() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .aspectRatio(WIDESCREEN_RATIO)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    @Composable
    private fun Loaded() {
        val model: PlayerScreenModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow
        val windowSizeClass = LocalWindowSizeClass.current

        if (model.showOptions) {
            PlayerOptionsMenu(onDismissRequest = model::hideOptions)
        }

        if (model.showDownloadDialog) {
            DownloadForm(onDismissRequest = model::hideDownloadDialog)
        }

        // On Desktop show the comments in a list, use a sheet on mobile
        // if (model.showCommentsSheet) {
        //     CommentsSheet(onDismissRequest = viewModel::hideComments)
        // }

        // DisposableEffect(model.isFullscreen) {
        //     val activity = context.findActivity()
        //     val insetsController =
        //         WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        //     val originalOrientation = activity.requestedOrientation
        //
        //     if (viewModel.isFullscreen) {
        //         insetsController.systemBarsBehavior =
        //             WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        //         insetsController.hide(WindowInsetsCompat.Type.systemBars())
        //     } else {
        //         insetsController.show(WindowInsetsCompat.Type.systemBars())
        //     }
        //
        //     activity.requestedOrientation = when {
        //         viewModel.isFullscreen -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        //         else -> originalOrientation
        //     }
        //
        //     onDispose {
        //         activity.requestedOrientation = originalOrientation
        //         insetsController.show(WindowInsetsCompat.Type.systemBars())
        //     }
        // }

        // @SuppressLint("SwitchIntDef")
        // when (LocalConfiguration.current.orientation) {
        //     Configuration.ORIENTATION_PORTRAIT -> PlayerScreenPortrait()
        //
        //     Configuration.ORIENTATION_LANDSCAPE -> PlayerControlsContainer(
        //         modifier = Modifier.fillMaxHeight()
        //     )
        // }

        Scaffold { paddingValues ->
            val content = remember {
                movableContentOf {
                    PlayerControlsContainer(onClickCollapse = navigator::pop)

                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            val video = model.video!!

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
                                                    navigator.push(TagScreen(badge))
                                                }
                                            )
                                        }
                                    }
                                }

                                Description(
                                    expanded = model.showFullDescription,
                                    onClick = model::toggleDescription,
                                    video = video
                                )

                                PlayerActions(
                                    modifier = Modifier.fillMaxWidth(),
                                    voteEnabled = model.accountManager.loggedIn,
                                    likeLabel = { Text(video.likesText) },
                                    dislikeLabel = { Text(model.dislikes.toString()) },
                                    showDownloadButton = model.preferences.showDownloadButton,
                                    onClickVote = model::updateVote,
                                    onClickShare = model::shareVideo,
                                    onClickDownload = model::showDownloadDialog
                                )

                                Card(
                                    onClick = { navigator.push(ChannelScreen(video.author.id)) }
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
                                            enabled = model.accountManager.loggedIn,
                                            onClick = model::toggleSubscription
                                        ) {
                                            Text(stringResource(MR.strings.subscribe))
                                        }
                                    }
                                }

                                Card(onClick = model::showComments) {
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
                    }
                }
            }

            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                if (windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Expanded) {
                    Row {
                        content()

                        if (model.preferences.showRelatedVideos) {
                            RelatedItems()
                        }
                    }
                } else {
                    Column {
                        content()
                    }
                }
            }
        }
    }

    @Composable
    private fun RelatedItems() {
        val model: PlayerScreenModel = koinScreenModel()
        val relatedVideos = model.relatedVideos.collectAsLazyPagingItems()
        val state = rememberLazyListState()

        LazyColumn(
            state = state
        ) {
            items(
                count = relatedVideos.itemCount,
                key = relatedVideos.itemKey { it.id },
                contentType = relatedVideos.itemContentType()
            ) { index ->
                val relatedVideo = relatedVideos[index] ?: return@items

                VideoCard(
                    video = relatedVideo,
                    onClick = { model.loadVideo(relatedVideo.id) }
                )
            }

            item {
                LoadingIndicator(relatedVideos.loadState)
            }
        }
    }

    @Composable
    private fun Description(expanded: Boolean, onClick: () -> Unit, video: DomainVideo) {
        val colorScheme = MaterialTheme.colorScheme
        val description = remember(video.description) {
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

                    addLink(
                        url = LinkAnnotation.Url(result.value),
                        start = result.range.first,
                        end = result.range.last + 1
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .animateContentSize()
                .height(if (expanded) Dp.Unspecified else 100.dp)
        ) {
            // SelectionContainer {
            val uriHandler = LocalUriHandler.current

            Text(
                modifier = Modifier.clickable(onClick = onClick),
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                overflow = TextOverflow.Ellipsis
            )
            // }

            ChaptersRow(
                video = video,
                onClickChapter = { /* TODO */ }
            )
        }
    }

    @Composable
    private fun ChaptersRow(video: DomainVideo, onClickChapter: (DomainChapter) -> Unit) {
        // Chapters
        BoxWithConstraints {
            if (maxHeight >= 160.dp) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = video.chapters,
                        key = DomainChapter::hashCode
                    ) { chapter ->
                        Chapter(
                            chapter = chapter,
                            onClick = { onClickChapter(chapter) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun Chapter(chapter: DomainChapter, onClick: () -> Unit) {
        OutlinedCard(
            modifier = Modifier.size(width = 144.dp, height = 160.dp),
            onClick = onClick
        ) {
            Column {
                ShimmerImage(
                    modifier = Modifier.aspectRatio(WIDESCREEN_RATIO),
                    url = chapter.thumbnail,
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    text = chapter.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    @Composable
    private fun PlayerControlsContainer(modifier: Modifier = Modifier, onClickCollapse: () -> Unit) {
        val model: PlayerScreenModel = koinScreenModel()
        val coroutineScope = rememberCoroutineScope()
        val interactionSource = remember { MutableInteractionSource() }
        val offsetY = remember { Animatable(0f) }

        val isHovered by interactionSource.collectIsHoveredAsState()

        LaunchedEffect(isHovered) {
            model.showControls = isHovered
        }

        Box(
            modifier = modifier
                .background(Color.Black)
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { model.toggleControls() },
                        onDoubleTap = { offset ->
                            if (offset.x > size.width / 2) {
                                model.seekForward()
                            } else {
                                model.seekBackward()
                            }
                        }
                    )
                }.hoverable(interactionSource)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { dragAmount ->
                        val offset = offsetY.value + dragAmount

                        if (model.isFullscreen &&
                            offset in 0f..200f ||
                            !model.isFullscreen &&
                            offset in -200f..0f
                        ) {
                            coroutineScope.launch { offsetY.snapTo(offset) }
                        }
                    },
                    onDragStopped = {
                        if (offsetY.value > 150 ||
                            offsetY.value < -150
                        ) {
                            model.toggleFullscreen()
                        }

                        offsetY.animateTo(0f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Player()

            AnimatedVisibility(
                modifier = Modifier
                    .size(64.dp)
                    .zIndex(99f),
                visible = model.playbackState == PlaybackState.Buffering,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator()
            }

            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = model.showControls,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    tonalElevation = 8.dp
                ) {
                    PlayerOverlay(
                        modifier = Modifier.matchParentSize(),
                        model = model,
                        onClickCollapse = onClickCollapse
                    )
                }
            }
        }
    }
}

@Composable
internal expect fun PlayerScreen.Player()