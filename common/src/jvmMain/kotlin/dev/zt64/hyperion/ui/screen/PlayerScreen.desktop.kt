package dev.zt64.hyperion.ui.screen

import androidx.compose.runtime.Composable
import dev.zt64.hyperion.ui.component.player.Player
import dev.zt64.hyperion.ui.component.player.rememberPlayerState

@Composable
internal actual fun PlayerScreen.Player() {
    Player(rememberPlayerState())
}