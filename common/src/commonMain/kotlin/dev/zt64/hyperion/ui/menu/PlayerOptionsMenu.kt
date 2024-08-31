package dev.zt64.hyperion.ui.menu

import androidx.compose.runtime.Composable
import dev.zt64.hyperion.ui.screen.PlayerScreen

@Composable
fun PlayerScreen.PlayerOptionsMenu(onDismissRequest: () -> Unit) {
    ContextMenu(
        menuItems = {
            PlayerOptionsMenuContent(onDismissRequest)
        },
        content = {}
    )
}

@Composable
internal expect fun PlayerScreen.PlayerOptionsMenuContent(onDismissRequest: () -> Unit)