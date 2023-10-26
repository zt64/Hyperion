package dev.zt64.hyperion.ui.component.player

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.model.IPlayerScreenModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerControls(
    model: IPlayerScreenModel,
    onClickCollapse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // val model: IPlayerScreenModel = getScreenModel()
    //
    PlayerControls(
        position = model.position,
        duration = model.duration,
        isPlaying = model.isPlaying,
        isFullscreen = model.isFullscreen,
        showCaptions = model.showCaptions,
        onClickCollapse = onClickCollapse,
        onClickFullscreen = model::toggleFullscreen,
        onClickSkipBackward = model::skipBackward,
        onClickSkipForward = model::skipForward,
        onClickPlayPause = model::togglePlay,
        onClickCaptions = model::toggleCaptions,
        onClickOptions = model::showOptions,
        onSeek = model::seekTo,
        modifier = modifier
    )
}

@Composable
fun PlayerControls(
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
    modifier: Modifier = Modifier,
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

                // SeekBar(
                //     modifier = Modifier.weight(1f),
                //     duration = duration,
                //     progress = if (isDragged) seekPosition else position,
                //     buffered = buffered,
                //     onSeek = { seekPosition = it },
                //     onSeekFinished = { onSeek(seekPosition) },
                //     interactionSource = interactionSource
                // )

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