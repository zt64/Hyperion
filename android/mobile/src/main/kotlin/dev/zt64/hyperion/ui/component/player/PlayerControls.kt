package dev.zt64.hyperion.ui.component.player

import android.text.format.DateUtils
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
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
import dev.zt64.hyperion.common.R
import dev.zt64.hyperion.ui.component.SeekBar
import dev.zt64.hyperion.ui.viewmodel.PlayerViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun PlayerControls(
    onClickCollapse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PlayerViewModel = koinViewModel()

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
            IconButton(onClick = viewModel::skipBackward) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = stringResource(MR.strings.skip_previous)
                )
            }

            IconButton(onClick = viewModel::togglePlayPause) {
                val image = AnimatedImageVector.animatedVectorResource(
                    id = R.drawable.ic_play_to_pause
                )

                Icon(
                    modifier = Modifier.size(50.dp),
                    painter = rememberAnimatedVectorPainter(image, viewModel.isPlaying),
                    contentDescription = stringResource(
                        resource = if (viewModel.isPlaying) MR.strings.pause else MR.strings.play
                    )
                )
            }

            IconButton(onClick = viewModel::skipNext) {
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
            IconButton(onClick = viewModel::toggleCaptions) {
                Icon(
                    imageVector = if (viewModel.showCaptions) {
                        Icons.Default.ClosedCaption
                    } else {
                        Icons.Default.ClosedCaptionOff
                    },
                    contentDescription = stringResource(MR.strings.captions)
                )
            }

            IconButton(onClick = viewModel::showOptions) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(MR.strings.more)
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = "${DateUtils.formatElapsedTime(viewModel.position.inWholeSeconds)} / ${
                    DateUtils.formatElapsedTime(viewModel.duration.inWholeSeconds)
                }",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                var seekPosition by remember { mutableStateOf(viewModel.position) }
                val interactionSource = remember { MutableInteractionSource() }
                val isDragged by interactionSource.collectIsDraggedAsState()
                val buffered by remember { derivedStateOf { viewModel.position + 10.seconds } }

                SeekBar(
                    modifier = Modifier.weight(1f),
                    duration = viewModel.duration,
                    progress = if (isDragged) seekPosition else viewModel.position,
                    buffered = buffered,
                    onSeek = { seekPosition = it },
                    onSeekFinished = { viewModel.seekTo(seekPosition) },
                    interactionSource = interactionSource
                )

                IconButton(onClick = viewModel::toggleFullscreen) {
                    if (viewModel.isFullscreen) {
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