package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.*
import dev.zt64.innertube.domain.model.DomainFormat
import kotlin.time.Duration

actual class PlayerScreenModel internal actual constructor(videoId: String) : AbstractPlayerScreenModel(videoId) {
    override var playbackState: PlaybackState by mutableStateOf(PlaybackState.Paused)
        private set

    override val isPlaying: Boolean by derivedStateOf { playbackState == PlaybackState.Playing }
    override val isLoading: Boolean by derivedStateOf { playbackState == PlaybackState.Buffering }
    override val repeatMode: Int by mutableIntStateOf(0)

    override fun updatePlayer() {
        TODO("Not yet implemented")
    }

    override fun skipForward() {
        TODO("Not yet implemented")
    }

    override fun skipBackward() {
        TODO("Not yet implemented")
    }

    override fun skipNext() {
        TODO("Not yet implemented")
    }

    override fun skipPrevious() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Duration) {
        TODO("Not yet implemented")
    }

    override fun togglePlay() {
        TODO("Not yet implemented")
    }

    override fun toggleLoop() {
        TODO("Not yet implemented")
    }

    override fun selectFormat(format: DomainFormat.Video) {
        TODO("Not yet implemented")
    }
}