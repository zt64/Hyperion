package dev.zt64.hyperion.ui.component.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR

@Composable
fun MiniPlayer(
    player: PlayerState,
    title: String,
    author: String,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onClickPlayPause: () -> Unit,
    onClickClose: () -> Unit,
    modifier: Modifier = Modifier
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
                // Player(player)

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

                FilledTonalIconButton(onClickPlayPause) {
                    if (isPlaying) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(MR.strings.play)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = stringResource(MR.strings.pause)
                        )
                    }
                }

                FilledTonalIconButton(onClickClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(MR.strings.close_mini_player)
                    )
                }
            }

            LinearProgressIndicator(
                progress = {
                    (player.position / player.duration).toFloat()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}