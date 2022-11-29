package com.hyperion.ui.component.player

import android.view.SurfaceView
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player

@Composable
fun Player(
    modifier: Modifier = Modifier,
    player: Player
) {
    AndroidView(
        modifier = Modifier
            .aspectRatio(16f / 9f)
            .then(modifier),
        factory = { context ->
            SurfaceView(context)
        },
        update = { surfaceView ->
            player.setVideoSurfaceView(surfaceView)
        }
    )
}