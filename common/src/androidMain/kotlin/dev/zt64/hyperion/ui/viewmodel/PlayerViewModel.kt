package dev.zt64.hyperion.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.*
import androidx.media3.common.Player.RepeatMode
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.paging.*
import com.google.common.util.concurrent.MoreExecutors
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.domain.manager.DownloadManager
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.domain.model.Rating
import dev.zt64.hyperion.domain.paging.BrowsePagingSource
import dev.zt64.hyperion.player.PlaybackService
import dev.zt64.innertube.domain.model.*
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import dev.zt64.innertube.network.service.InnerTubeService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
class PlayerViewModel(
    private val application: Application,
    private val innerTube: InnerTubeRepository,
    private val downloadManager: DownloadManager,
    private val pagingConfig: PagingConfig,
    val accountManager: AccountManager,
    val preferences: PreferencesManager,
    videoId: String
) : ViewModel() {
    @Immutable
    sealed interface State {
        data object Loaded : State
        data object Loading : State
        data class Error(val exception: Exception) : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set
    var video by mutableStateOf<DomainVideo?>(null)
        private set
    var videoFormats = mutableStateListOf<DomainFormat.Video>()
        private set
    private var audioSource: ProgressiveMediaSource? = null
    var videoFormat: DomainFormat.Video? = null
    var isFullscreen by mutableStateOf(false)
        private set
    var showFullDescription by mutableStateOf(false)
        private set
    var showControls by mutableStateOf(false)
        private set
    var showQualityPicker by mutableStateOf(false)
        private set
    var showDownloadDialog by mutableStateOf(false)
        private set
    var showCaptions by mutableStateOf(false)
        private set
    var showCommentsSheet by mutableStateOf(false)

    private val listener: Player.Listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            this@PlayerViewModel.isPlaying = isPlaying
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            this@PlayerViewModel.isLoading = isLoading
        }

        override fun onPlaybackStateChanged(@Player.State playbackState: Int) {
            this@PlayerViewModel.playbackState = playbackState
        }

        override fun onPlayerError(error: PlaybackException) {
            state = State.Error(error)
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            this@PlayerViewModel.repeatMode = repeatMode
        }

        override fun onTracksChanged(tracks: Tracks) {
            this@PlayerViewModel.tracks = tracks
        }
    }

    private val dataSourceFactory = DefaultHttpDataSource.Factory()
    private val liveliness = Channel<Unit>()

    var isPlaying by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
        private set

    @get:Player.State
    var playbackState by mutableIntStateOf(Player.STATE_IDLE)
        private set
    var duration by mutableStateOf(Duration.ZERO)
        private set
    var position by mutableStateOf(Duration.ZERO)
        private set
    var speed by mutableFloatStateOf(1f)
        private set

    @get:RepeatMode
    var repeatMode by mutableIntStateOf(Player.REPEAT_MODE_OFF)
        private set
    var tracks by mutableStateOf(Tracks.EMPTY)
        private set

    var relatedVideos = emptyFlow<PagingData<DomainVideoPartial>>()
        private set
    var comments = emptyFlow<PagingData<DomainComment>>()
        private set

    lateinit var player: MediaController

    init {
        viewModelScope.launch {
            loadVideo(videoId)
        }

        val sessionToken =
            SessionToken(application, ComponentName(application, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()

        controllerFuture.addListener(
            /* listener = */ {
                player = controllerFuture.get()
                player.addListener(listener)
                player.prepare()

                viewModelScope.launch {
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

    override fun onCleared() {
        player.release()
    }

    fun shareVideo() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"

            putExtra(Intent.EXTRA_TEXT, video!!.shareUrl)
            putExtra(Intent.EXTRA_TITLE, video!!.title)
        }

        application.startActivity(Intent.createChooser(shareIntent, null).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    fun skipForward() = player.seekForward()
    fun skipBackward() = player.seekBack()
    fun skipNext() = player.seekToNext()
    fun skipPrevious() = player.seekToPrevious()
    fun seekTo(position: Duration) = player.seekTo(position.inWholeMilliseconds)

    fun toggleDescription() {
        showFullDescription = !showFullDescription
    }

    fun toggleControls() {
        showControls = !showControls
    }

    fun togglePlayPause() {
        player.playWhenReady = !player.playWhenReady
    }

    fun toggleFullscreen() {
        isFullscreen = !isFullscreen
    }

    fun showOptions() {
        showQualityPicker = true
    }

    fun hideOptions() {
        showQualityPicker = false
    }

    fun showDownloadDialog() {
        showDownloadDialog = true
    }

    fun hideDownloadDialog() {
        showDownloadDialog = false
    }

    fun setPlaybackSpeed(speed: Float) {
        this.speed = speed
        player.setPlaybackSpeed(speed)
    }

    fun updateVote(rating: Rating) {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleCaptions() {
        showCaptions = !showCaptions
    }

    fun download() {
        viewModelScope.launch {
            try {
                downloadManager
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setFormat(format: DomainFormat.Video) {
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

    fun loadVideo(id: String) {
        state = State.Loading

        viewModelScope.launch {
            try {
                video = innerTube.getVideo(id)

                state = State.Loaded

                videoFormats.clear()
                videoFormats.addAll(video!!.formats.filterIsInstance<DomainFormat.Video>())
                videoFormat = videoFormats.first()

                val audioStream = video!!.formats.filterIsInstance<DomainFormat.Audio>().last()
                audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(audioStream.url))

                setFormat(videoFormat!!)

                relatedVideos = Pager(pagingConfig) {
                    BrowsePagingSource { key ->
                        if (key == null) {
                            innerTube.getNext(video!!.id).relatedVideos
                        } else {
                            innerTube.getRelatedVideos(video!!.id, key)
                        }
                    }
                }.flow.cachedIn(viewModelScope)
            } catch (e: Exception) {
                e.printStackTrace()
                state = State.Error(e)
            }
        }
    }

    fun toggleSubscription() {
        viewModelScope.launch {
            try {
                val channelId = video!!.author.id
                // Make request to subscribe
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showComments() {
        showCommentsSheet = true
    }

    fun hideComments() {
        showCommentsSheet = false
    }

    fun selectFormat(format: DomainFormat.Video) {
        setFormat(format)
        hideOptions()
    }

    fun toggleLoop() {
        player.repeatMode = if (player.repeatMode == Player.REPEAT_MODE_ONE) {
            Player.REPEAT_MODE_OFF
        } else {
            Player.REPEAT_MODE_ONE
        }
        showQualityPicker = false
    }
}