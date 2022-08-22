package com.hyperion.ui.component

import android.text.format.DateUtils
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import kotlin.time.Duration

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    isFullscreen: Boolean,
    isPlaying: Boolean,
    position: Duration,
    duration: Duration,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onClickCollapse: () -> Unit,
    onClickPlayPause: () -> Unit,
    onClickFullscreen: () -> Unit,
    onClickMore: () -> Unit,
    onSeek: (ms: Float) -> Unit
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
                contentDescription = stringResource(R.string.minimize)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSkipPrevious) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = stringResource(R.string.skip_previous)
                )
            }

            IconButton(
                modifier = Modifier.size(64.dp),
                onClick = onClickPlayPause
            ) {
                if (isPlaying) {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Icons.Default.Pause,
                        contentDescription = stringResource(R.string.pause)
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(R.string.play)
                    )
                }
            }

            IconButton(onClick = onSkipNext) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = stringResource(R.string.skip_next)
                )
            }
        }

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onClickMore
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.more)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = "${DateUtils.formatElapsedTime(position.inWholeSeconds)} / ${DateUtils.formatElapsedTime(duration.inWholeSeconds)}",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                var seekPosition by remember { mutableStateOf(position.inWholeMilliseconds) }
                val interactionSource = remember { MutableInteractionSource() }
                val isDragged by interactionSource.collectIsDraggedAsState()

                Slider(
                    modifier = Modifier.weight(1f, true),
                    interactionSource = interactionSource,
                    value = if (isDragged) seekPosition.toFloat() else position.inWholeMilliseconds.toFloat(),
                    valueRange = 0f..duration.inWholeMilliseconds.toFloat(),
                    onValueChange = { seekPosition = it.toLong() },
                    onValueChangeFinished = { onSeek(seekPosition.toFloat()) }
                )

                IconButton(
                    onClick = onClickFullscreen
                ) {
                    if (isFullscreen) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = stringResource(R.string.close_fullscreen)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = stringResource(R.string.open_fullscreen)
                        )
                    }
                }
            }
        }

    }
}