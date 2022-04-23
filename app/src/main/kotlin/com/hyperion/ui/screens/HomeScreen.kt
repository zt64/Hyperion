package com.hyperion.ui.screens

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hyperion.R
import com.hyperion.ui.components.VideoCard
import com.hyperion.ui.screens.destinations.ChannelScreenDestination
import com.hyperion.ui.screens.destinations.PlayerScreenDestination
import com.hyperion.ui.viewmodel.MainViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigateTo
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Destination(
    start = true
)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(viewModel.state.isLoading),
            onRefresh = viewModel::fetchTrending,
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    scale = true,
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state is MainViewModel.State.Loaded) {
                    items(state.videos) { trendingVideo ->
                        VideoCard(
                            video = trendingVideo,
                            onClick = {
                                navController.navigateTo(
                                    PlayerScreenDestination(
                                        trendingVideo.videoId
                                    )
                                )
                            },
                            onChannelClick = {
                                navController.navigateTo(
                                    ChannelScreenDestination(
                                        trendingVideo.authorId
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }

//        Player(
//            modifier = Modifier.align(Alignment.BottomCenter),
//            onExpand = {
//
//            }
//        )
    }
}

private enum class PlayerState {
    EXPANDED, COLLAPSED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Player(
    modifier: Modifier = Modifier,
    onExpand: () -> Unit
) {
    BoxWithConstraints {
        val fullHeight = constraints.maxHeight.toFloat()

        var height by remember { mutableStateOf(0f) }
        val swipeableState = rememberSwipeableState(initialValue = PlayerState.EXPANDED)
        val anchors = mapOf(
            fullHeight - height to PlayerState.COLLAPSED,
            fullHeight / 2 to PlayerState.EXPANDED
        )

        Surface(
            modifier = modifier
                .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
                .onGloballyPositioned {
                    height = it.size.height.toFloat()
                }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    orientation = Orientation.Vertical
                ),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://s1.best-wallpaper.net/wallpaper/m/1703/Capybara-close-up-face_m.webp")
                        .crossfade(true)
                        .build(),
                    contentDescription = null
                )

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Video Title",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "3:23 / 8:30",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(Modifier.weight(1f, true))

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.play)
                        )
                    }

                    IconButton(onClick = onExpand) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = stringResource(R.string.fullscreen)
                        )
                    }
                }
            }
        }
    }
}