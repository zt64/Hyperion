package dev.zt64.hyperion.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.koin.getScreenModel
import dev.zt64.hyperion.ui.component.player.Player
import dev.zt64.hyperion.ui.component.player.rememberPlayerState
import dev.zt64.hyperion.ui.model.PlayerScreenModel

@Composable
internal actual fun PlayerScreen.Player() {
    val model = getScreenModel<PlayerScreenModel>()
    val state = rememberPlayerState(model.player)

    Player(state)
}