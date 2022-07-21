package com.hyperion.ui.component

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@Composable
fun Player(
    modifier: Modifier = Modifier,
    player: Player,
) = AndroidView(
    modifier = Modifier
        .aspectRatio(16f / 9f)
        .then(modifier),
    factory = { context ->
        PlayerView(context).apply {
            this.player = player
            useController = false
            useArtwork = true
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
    }
)