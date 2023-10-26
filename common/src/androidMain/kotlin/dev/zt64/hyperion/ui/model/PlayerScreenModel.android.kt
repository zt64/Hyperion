package dev.zt64.hyperion.ui.model

import android.app.Application
import android.content.ComponentName
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import cafe.adriel.voyager.core.model.coroutineScope
import com.google.common.util.concurrent.MoreExecutors
import dev.zt64.hyperion.player.PlaybackService
import dev.zt64.innertube.domain.model.DomainFormat
import dev.zt64.innertube.network.service.InnerTubeService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.get
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@UnstableApi
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

    override var isLoading: Boolean by mutableStateOf(false)
        private set
    override var isPlaying: Boolean by mutableStateOf(false)
        private set
    override var playbackState: PlaybackState by mutableStateOf(PlaybackState.Paused)
        private set
    // @get:Player.State
    // override var playbackState by mutableIntStateOf(Player.STATE_IDLE)
    //     private set

    @get:Player.RepeatMode
    override var repeatMode by mutableIntStateOf(Player.REPEAT_MODE_OFF)
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
            /* listener = */ {
                player = controllerFuture.get()
                player.addListener(listener)
                player.prepare()

                coroutineScope.launch {
                    while (isActive) {
                        duration = player.duration.takeUnless { it == C.TIME_UNSET }?.milliseconds
                            ?: Duration.ZERO
                        position = player.currentPosition.milliseconds
                        delay(500)
                    }
                }
            },
            /* executor = */ MoreExecutors.directExecutor()
        )
    }

    override fun onDispose() {
        player.release()
    }

    override fun skipForward() = player.seekForward()
    override fun skipBackward() = player.seekBack()
    override fun skipNext() = player.seekToNext()
    override fun skipPrevious() = player.seekToPrevious()
    override fun seekTo(position: Duration) = player.seekTo(position.inWholeMilliseconds)
    override fun togglePlay() {
        player.playWhenReady = !player.playWhenReady
    }

    override fun setPlaybackSpeed(speed: Float) {
        super.setPlaybackSpeed(speed)
        player.setPlaybackSpeed(speed)
    }

    override fun selectFormat(format: DomainFormat.Video) {
        super.selectFormat(format)
        hideOptions()
    }

    override fun toggleLoop() {
        player.repeatMode = if (player.repeatMode == Player.REPEAT_MODE_ONE) {
            Player.REPEAT_MODE_OFF
        } else {
            Player.REPEAT_MODE_ONE
        }
        showQualityPicker = false
    }

    override fun updatePlayer() {
        val metadata = MediaMetadata.Builder()
            .setTitle(video!!.title)
            .setArtist(video!!.author.name)
            .setMediaType(MediaMetadata.MEDIA_TYPE_VIDEO)
            .setIsPlayable(true)
            .setArtworkUri(InnerTubeService.getVideoThumbnail(video!!.id).toUri())
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(video!!.id)
            .setMediaMetadata(metadata)
            .build()

        player.setMediaItem(mediaItem)
    }
}