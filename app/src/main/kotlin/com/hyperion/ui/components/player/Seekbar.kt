package com.hyperion.ui.components.player

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.Player

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
            modifier = modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = DateUtils.formatElapsedTime(player.currentPosition / 1000),
                style = MaterialTheme.typography.labelMedium
            )
            Slider(
                modifier = Modifier.weight(1f, true),
                value = player.currentPosition.toFloat(),
                valueRange = 0f..player.duration.toFloat(),
                onValueChange = { player.seekTo(it.toLong()) }
            )
            Text(
                text = DateUtils.formatElapsedTime(player.duration / 1000),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}