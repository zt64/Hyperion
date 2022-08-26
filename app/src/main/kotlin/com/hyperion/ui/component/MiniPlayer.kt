package com.hyperion.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.hyperion.R

@Composable
fun MiniPlayer(
    modifier: Modifier = Modifier,
    player: Player,
    title: String,
    author: String,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onClickPlayPause: () -> Unit,
    onClickClose: () -> Unit
) {
    Surface(
        modifier = modifier,
        tonalElevation = 8.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .height(70.dp)
                    .clickable(
                        onClick = onClick,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Player(player = player)

                Spacer(Modifier.width(4.dp))

                Column(
                    modifier = Modifier.weight(1f, true)
                ) {
                    Text(
                        text = title,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = author,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                FilledTonalIconButton(onClick = onClickPlayPause) {
                    if (isPlaying) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.play)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = stringResource(R.string.pause)
                        )
                    }
                }

                FilledTonalIconButton(onClick = onClickClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_mini_player)
                    )
                }
            }

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = (player.contentPosition / player.contentDuration).toFloat(),
            )
        }
    }
}