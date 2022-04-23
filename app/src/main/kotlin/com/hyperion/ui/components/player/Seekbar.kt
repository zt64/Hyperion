package com.hyperion.ui.components.player

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.Player
import com.hyperion.R

@Composable
fun Seekbar(
    modifier: Modifier = Modifier,
    player: Player
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var position by remember { mutableStateOf(0L) }

            Text(
                text = buildString {
                    append(DateUtils.formatElapsedTime(player.currentPosition / 1000))
                    append(" / ")
                    append(DateUtils.formatElapsedTime(player.duration / 1000))
                },
                style = MaterialTheme.typography.labelMedium
            )
            Slider(
                modifier = Modifier.weight(1f, true),
                value = player.currentPosition.toFloat(),
                valueRange = 0f..player.duration.toFloat(),
                onValueChange = { position = it.toLong() },
                onValueChangeFinished = { player.seekTo(position) }
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = stringResource(R.string.fullscreen)
                )
            }
        }
    }
}