package dev.zt64.hyperion.ui.component.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zt64.hyperion.ui.viewmodel.PlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerControls(
    onClickCollapse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PlayerViewModel = koinViewModel()

    PlayerControls(
        position = viewModel.position,
        duration = viewModel.duration,
        isPlaying = viewModel.isPlaying,
        isFullscreen = viewModel.isFullscreen,
        showCaptions = viewModel.showCaptions,
        onClickCollapse = onClickCollapse,
        onClickFullscreen = viewModel::toggleFullscreen,
        onClickSkipBackward = viewModel::skipBackward,
        onClickSkipForward = viewModel::skipForward,
        onClickPlayPause = viewModel::togglePlayPause,
        onClickCaptions = viewModel::toggleCaptions,
        onClickOptions = viewModel::showOptions,
        onSeek = viewModel::seekTo,
        modifier = modifier
    )
}