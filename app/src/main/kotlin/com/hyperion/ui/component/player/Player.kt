package com.hyperion.ui.component.player

import android.view.SurfaceView
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi

const val WIDESCREEN_RATIO = 16f / 9f

@Composable
fun Player(
    player: Player,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = Modifier
            .aspectRatio(WIDESCREEN_RATIO)
            .then(modifier),
        factory = { context ->
            SurfaceView(context)
        },
        update = { surfaceView ->
            player.setVideoSurfaceView(surfaceView)
        }
    )
}