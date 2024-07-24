package dev.zt64.hyperion.ui.model

import dev.zt64.innertube.domain.model.DomainFormat
import kotlin.time.Duration

actual class PlayerScreenModel internal actual constructor(videoId: String) :
    AbstractPlayerScreenModel(videoId) {
        // override val isPlaying: Boolean by derivedStateOf { playbackState == PlaybackState.Playing }
        // override val isLoading: Boolean by derivedStateOf {
        //     playbackState == PlaybackState.Buffering
        // }
        // override val repeatMode: Int by mutableIntStateOf(0)

        override fun updatePlayer() {
        }

        actual fun seekForward() {
            TODO("Not yet implemented")
        }

        actual fun seekBackward() {
            TODO("Not yet implemented")
        }

        actual fun skipNext() {
            TODO("Not yet implemented")
        }

        actual fun skipPrevious() {
            TODO("Not yet implemented")
        }

        actual override fun seekTo(position: Duration) {
        }

        actual override fun togglePlay() {
            TODO("Not yet implemented")
        }

        override fun toggleLoop() {
            TODO("Not yet implemented")
        }

        actual override fun selectFormat(format: DomainFormat.Video) {
            TODO("Not yet implemented")
        }
    }