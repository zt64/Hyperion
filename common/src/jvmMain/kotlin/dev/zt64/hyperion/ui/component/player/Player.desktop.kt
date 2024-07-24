package dev.zt64.hyperion.ui.component.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.time.Duration

@Composable
actual fun Player(
    state: PlayerState,
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .widthIn(max = 1280.dp)
            .aspectRatio(WIDESCREEN_RATIO)
            .background(Color.Black)
    )
}

@Composable
fun rememberPlayerState(): PlayerState = remember { PlayerState() }

@Stable
actual class PlayerState {
    actual var repeatMode: Int by mutableIntStateOf(0)
        private set

    actual var position by mutableStateOf(Duration.ZERO)
        private set

    actual var duration by mutableStateOf(Duration.ZERO)
        private set
}