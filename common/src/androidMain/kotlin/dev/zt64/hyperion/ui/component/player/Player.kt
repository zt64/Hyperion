package dev.zt64.hyperion.ui.component.player

import android.view.SurfaceView
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
            SurfaceView(context).apply {
                player.setVideoSurfaceView(this)
            }
        }
    )
}

@Composable
fun rememberPlayerState(player: Player): PlayerState {
    return rememberSaveable { PlayerState(player) }
}

@Stable
class PlayerState(player: Player) {
    var isPlaying by mutableStateOf(player.isPlaying)
        private set

    var isLoading by mutableStateOf(player.isLoading)
        private set

    var tracks by mutableStateOf(player.currentTracks)
        private set

    @get:Player.RepeatMode
    var repeatMode by mutableIntStateOf(player.repeatMode)
        private set

    @get:Player.State
    var playbackState by mutableIntStateOf(player.playbackState)
        private set

    var seekBackIncrementMs by mutableLongStateOf(player.seekBackIncrement)
        private set

    var seekForwardIncrementMs by mutableLongStateOf(player.seekForwardIncrement)
        private set

    @get:FloatRange(from = 0.0, to = 1.0)
    var volume by mutableFloatStateOf(player.volume)
        private set

    @delegate:UnstableApi
    var surfaceSize by mutableStateOf(player.surfaceSize)
        private set

    init {
        player.addListener(EventLogger())
    }

    private inner class EventLogger : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            this@PlayerState.isPlaying = isPlaying
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            this@PlayerState.isLoading = isLoading
        }

        override fun onTracksChanged(tracks: Tracks) {
            this@PlayerState.tracks = tracks
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            this@PlayerState.repeatMode = repeatMode
        }

        override fun onPlaybackStateChanged(state: Int) {
            this@PlayerState.playbackState = state
        }

        override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
            this@PlayerState.seekBackIncrementMs = seekBackIncrementMs
        }

        override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
            this@PlayerState.seekForwardIncrementMs = seekForwardIncrementMs
        }

        override fun onVolumeChanged(volume: Float) {
            this@PlayerState.volume = volume
        }

        @UnstableApi
        override fun onSurfaceSizeChanged(width: Int, height: Int) {
            this@PlayerState.surfaceSize = Size(width, height)
        }
    }
}