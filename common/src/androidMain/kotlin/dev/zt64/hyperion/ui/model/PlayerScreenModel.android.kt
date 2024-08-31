package dev.zt64.hyperion.ui.model

import android.app.Application
import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import cafe.adriel.voyager.core.model.screenModelScope
import com.google.common.util.concurrent.MoreExecutors
import dev.zt64.hyperion.api.domain.model.DomainFormat
import dev.zt64.hyperion.api.network.service.InnerTubeService
import dev.zt64.hyperion.player.PlaybackService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.get
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
actual class PlayerScreenModel internal actual constructor(videoId: String) : AbstractPlayerScreenModel(videoId) {
    private var audioSource: ProgressiveMediaSource? = null

    private val listener: Player.Listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            this@PlayerScreenModel.isPlaying = isPlaying
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            this@PlayerScreenModel.isLoading = isLoading
        }

        override fun onPlaybackStateChanged(@Player.State playbackState: Int) {
            // this@PlayerScreenModel.playbackState = playbackState
        }

        override fun onPlayerError(error: PlaybackException) {
            state = PlayerState.Error(error)
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            this@PlayerScreenModel.repeatMode = repeatMode
        }

        override fun onTracksChanged(tracks: Tracks) {
            this@PlayerScreenModel.tracks = tracks
        }
    }

    private val dataSourceFactory = DefaultHttpDataSource.Factory()
    private val liveliness = Channel<Unit>()

    // @get:Player.State
    // override var playbackState by mutableIntStateOf(Player.STATE_IDLE)
    //     private set

    @get:Player.RepeatMode
    var repeatMode by mutableIntStateOf(Player.REPEAT_MODE_OFF)
        private set

    var tracks by mutableStateOf(Tracks.EMPTY)
        private set

    lateinit var player: MediaController
        private set

    init {
        val application = get<Application>()
        val sessionToken =
            SessionToken(application, ComponentName(application, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                player = controllerFuture.get()
                player.addListener(listener)
                player.prepare()

                screenModelScope.launch {
                    while (isActive) {
                        duration =
                            player.duration.takeUnless { it == C.TIME_UNSET }?.milliseconds
                                ?: Duration.ZERO
                        position = player.currentPosition.milliseconds
                        delay(500)
                    }
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onDispose() {
        player.release()
    }

    actual fun seekForward() = player.seekForward()

    actual fun seekBackward() = player.seekBack()

    actual fun skipNext() = player.seekToNext()

    actual fun skipPrevious() = player.seekToPrevious()

    actual override fun seekTo(position: Duration) = player.seekTo(position.inWholeMilliseconds)

    actual override fun togglePlay() {
        player.playWhenReady = !player.playWhenReady
    }

    actual override fun setPlaybackSpeed(speed: Float) {
        super.setPlaybackSpeed(speed)
        player.setPlaybackSpeed(speed)
    }

    actual override fun selectFormat(format: DomainFormat.Video) {
        super.selectFormat(format)
        hideOptions()
    }

    override fun toggleLoop() {
        player.repeatMode = if (player.repeatMode == Player.REPEAT_MODE_ONE) {
            Player.REPEAT_MODE_OFF
        } else {
            Player.REPEAT_MODE_ONE
        }
        showOptions = false
    }

    override fun updatePlayer() {
        val metadata = MediaMetadata
            .Builder()
            .setTitle(video!!.title)
            .setArtist(video!!.author.name)
            .setMediaType(MediaMetadata.MEDIA_TYPE_VIDEO)
            .setIsPlayable(true)
            .setArtworkUri(InnerTubeService.getVideoThumbnail(video!!.id).toUri())
            .build()

        val mediaItem = MediaItem
            .Builder()
            .setMediaId(video!!.id)
            .setMediaMetadata(metadata)
            .build()

        player.setMediaItem(mediaItem)
    }
}