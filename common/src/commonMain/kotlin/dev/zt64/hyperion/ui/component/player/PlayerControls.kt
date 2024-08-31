package dev.zt64.hyperion.ui.component.player

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionOff
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.SeekBar
import dev.zt64.hyperion.ui.model.PlayerScreenModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerOverlay(model: PlayerScreenModel, onClickCollapse: () -> Unit, modifier: Modifier = Modifier) {
    PlayerOverlay(
        position = model.position,
        duration = model.duration,
        isPlaying = model.isPlaying,
        isFullscreen = model.isFullscreen,
        showCaptions = model.showCaptions,
        onClickCollapse = onClickCollapse,
        onClickFullscreen = model::toggleFullscreen,
        onClickSkipBackward = model::seekBackward,
        onClickSkipForward = model::seekForward,
        onClickPlayPause = model::togglePlay,
        onClickCaptions = model::toggleCaptions,
        onClickOptions = model::showOptions,
        onSeek = model::seekTo,
        modifier = modifier
    )
}

@Composable
fun PlayerOverlay(
    position: Duration,
    duration: Duration,
    isPlaying: Boolean,
    isFullscreen: Boolean,
    showCaptions: Boolean,
    onClickCollapse: () -> Unit,
    onClickFullscreen: () -> Unit,
    onClickSkipBackward: () -> Unit,
    onClickSkipForward: () -> Unit,
    onClickPlayPause: () -> Unit,
    onClickCaptions: () -> Unit,
    onClickOptions: () -> Unit,
    onSeek: (Duration) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(6.dp)
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.TopStart),
            onClick = onClickCollapse
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = stringResource(MR.strings.minimize)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClickSkipBackward) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = stringResource(MR.strings.skip_previous)
                )
            }

            IconButton(onClick = onClickPlayPause) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    imageVector = if (isPlaying) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    },
                    contentDescription = stringResource(
                        resource = if (isPlaying) MR.strings.pause else MR.strings.play
                    )
                )
            }

            IconButton(onClick = onClickSkipForward) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = stringResource(MR.strings.skip_next)
                )
            }
        }

        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onClickCaptions) {
                Icon(
                    imageVector = if (showCaptions) {
                        Icons.Default.ClosedCaption
                    } else {
                        Icons.Default.ClosedCaptionOff
                    },
                    contentDescription = stringResource(MR.strings.captions)
                )
            }

            IconButton(onClick = onClickOptions) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(MR.strings.more)
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val elapsedTime by remember(position) {
                derivedStateOf(position::formatElapsedTime)
            }

            val formattedDuration by remember(duration) {
                derivedStateOf(duration::formatElapsedTime)
            }

            Text(
                text = "$elapsedTime / $formattedDuration",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val isDragged by interactionSource.collectIsDraggedAsState()
                var seekPosition by remember { mutableStateOf(position) }
                val buffered by remember { derivedStateOf { position + 10.seconds } }

                SeekBar(
                    modifier = Modifier.weight(1f),
                    duration = duration,
                    progress = if (isDragged) seekPosition else position,
                    buffered = buffered,
                    onSeek = { seekPosition = it },
                    onSeekFinished = { onSeek(seekPosition) },
                    interactionSource = interactionSource
                )

                IconButton(onClick = onClickFullscreen) {
                    if (isFullscreen) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = stringResource(MR.strings.close_fullscreen)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = stringResource(MR.strings.open_fullscreen)
                        )
                    }
                }
            }
        }
    }
}

private fun Duration.formatElapsedTime(): String {
    return toComponents { hours, minutes, seconds, _ ->
        if (hours > 0) {
            "%d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }
}