package dev.zt64.hyperion.ui.component.player

import androidx.annotation.FloatRange
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
actual fun Player(
    state: PlayerState,
    modifier: Modifier
) {
    AndroidExternalSurface(
        modifier = Modifier
            .aspectRatio(WIDESCREEN_RATIO)
            .then(modifier)
    ) {
        onSurface { surface, _, _ -> state.player.setVideoSurface(surface) }
    }
}

@Composable
fun rememberPlayerState(player: Player): PlayerState = remember { PlayerState(player) }

@Stable
actual class PlayerState(internal val player: Player) :
    CoroutineScope by CoroutineScope(Dispatchers.Main) {
    var isPlaying by mutableStateOf(player.isPlaying)
        private set

    var isLoading by mutableStateOf(player.isLoading)
        private set

    var tracks by mutableStateOf(player.currentTracks)
        private set

    @get:Player.RepeatMode
    actual var repeatMode by mutableIntStateOf(player.repeatMode)
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

    actual var position by mutableStateOf(Duration.ZERO)
        private set

    actual var duration by mutableStateOf(Duration.ZERO)
        private set

    init {
        player.addListener(EventLogger())

        launch {
            while (isActive) {
                position = player.currentPosition.milliseconds
                duration = player.duration.milliseconds

                delay(100)
            }
        }
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
        override fun onSurfaceSizeChanged(
            width: Int,
            height: Int
        ) {
            this@PlayerState.surfaceSize = Size(width, height)
        }
    }
}