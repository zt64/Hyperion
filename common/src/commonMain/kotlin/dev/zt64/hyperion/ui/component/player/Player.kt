package dev.zt64.hyperion.ui.component.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import kotlin.time.Duration

const val WIDESCREEN_RATIO = 16f / 9f

@Composable
expect fun Player(
    state: PlayerState,
    modifier: Modifier = Modifier
)

@Stable
expect class PlayerState {
    var repeatMode: Int
        private set
    var duration: Duration
        private set
    var position: Duration
        private set
}