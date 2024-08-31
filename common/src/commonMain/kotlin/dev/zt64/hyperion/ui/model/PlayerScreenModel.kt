package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.zt64.hyperion.api.domain.model.DomainComment
import dev.zt64.hyperion.api.domain.model.DomainFormat
import dev.zt64.hyperion.api.domain.model.DomainVideo
import dev.zt64.hyperion.api.domain.model.DomainVideoPartial
import dev.zt64.hyperion.api.domain.repository.InnerTubeRepository
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.domain.manager.DownloadManager
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.domain.manager.ShareManager
import dev.zt64.hyperion.domain.model.Rating
import dev.zt64.hyperion.domain.paging.BrowsePagingSource
import dev.zt64.ryd.RydClient
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.Duration

@Stable
enum class PlaybackState {
    Buffering,
    Playing,
    Paused,
    Idle
}

@Immutable
sealed interface PlayerState {
    data object Loaded : PlayerState

    data object Loading : PlayerState

    data class Error(val exception: Exception) : PlayerState
}

expect class PlayerScreenModel internal constructor(videoId: String) : AbstractPlayerScreenModel {
    fun seekForward()

    fun seekBackward()

    fun skipNext()

    fun skipPrevious()

    override fun seekTo(position: Duration)

    override fun setPlaybackSpeed(speed: Float)

    override fun selectFormat(format: DomainFormat.Video)

    override fun togglePlay()
}

/**
 * Common screen model for the player screen that handles logic
 *
 * @property videoId
 */
abstract class AbstractPlayerScreenModel(private val videoId: String) :
    ScreenModel,
    KoinComponent {
    val accountManager = get<AccountManager>()
    val preferences = get<PreferencesManager>()

    private val rydClient = get<RydClient>()
    private val downloadManager = get<DownloadManager>()
    private val innerTube = get<InnerTubeRepository>()
    private val shareManager = get<ShareManager>()
    private val pagingConfig = get<PagingConfig>()

    var state by mutableStateOf<PlayerState>(PlayerState.Loading)
        protected set

    // queue
    // var videoQueue = mutableStateListOf<DomainVideo>()
    //     private set

    var video by mutableStateOf<DomainVideo?>(null)
        protected set
    var dislikes by mutableIntStateOf(0)
        private set

    var videoFormats = mutableStateListOf<DomainFormat.Video>()
        private set
    var videoFormat: DomainFormat.Video? = null
        private set

    var isFullscreen by mutableStateOf(false)
        private set
    var showFullDescription by mutableStateOf(false)
        private set
    var showControls by mutableStateOf(false)
    var showOptions by mutableStateOf(false)
        protected set
    var showDownloadDialog by mutableStateOf(false)
        private set
    var showCaptions by mutableStateOf(false)
        private set
    var showCommentsSheet by mutableStateOf(false)
        private set
    var duration by mutableStateOf(Duration.ZERO)
        protected set
    var position by mutableStateOf(Duration.ZERO)
        protected set
    var speed by mutableFloatStateOf(1f)
        private set

    var relatedVideos = emptyFlow<PagingData<DomainVideoPartial>>()
        private set
    var comments = emptyFlow<PagingData<DomainComment>>()
        private set

    var isLoading: Boolean by mutableStateOf(false)
        protected set

    var isPlaying: Boolean by mutableStateOf(false)
        protected set

    var loop: Boolean by mutableStateOf(false)
        protected set

    var playbackState: PlaybackState by mutableStateOf(PlaybackState.Idle)
        protected set

    init {
        loadVideo(videoId)
    }

    fun loadVideo(id: String) {
        screenModelScope.launch {
            try {
                state = PlayerState.Loading

                video = innerTube.getVideo(id)
                dislikes = rydClient.get(videoId).dislikes

                state = PlayerState.Loaded

                videoFormats.clear()
                videoFormats.addAll(
                    video!!.streamingData.formats.filterIsInstance<DomainFormat.Video>()
                )
                videoFormat = videoFormats.first()

                // val audioStream = video!!.formats.filterIsInstance<DomainFormat.Audio>().last()
                // audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                //     .createMediaSource(MediaItem.fromUri(audioStream.url))
                //
                // setFormat(videoFormat!!)
                setRelatedVideos()
                updatePlayer()
            } catch (e: Exception) {
                e.printStackTrace()
                state = PlayerState.Error(e)
            }
        }
    }

    open fun selectFormat(format: DomainFormat.Video) {
        updatePlayer()
    }

    fun showComments() {
        showCommentsSheet = true
    }

    fun hideComments() {
        showCommentsSheet = false
    }

    fun showOptions() {
        showOptions = true
    }

    fun hideOptions() {
        showOptions = false
    }

    fun showDownloadDialog() {
        showDownloadDialog = true
    }

    fun hideDownloadDialog() {
        showDownloadDialog = false
    }

    fun toggleDescription() {
        showFullDescription = !showFullDescription
    }

    fun toggleControls() {
        showControls = !showControls
    }

    fun toggleFullscreen() {
        isFullscreen = !isFullscreen
    }

    fun toggleCaptions() {
        showCaptions = !showCaptions
    }

    fun shareVideo() {
        shareManager.share(video!!.shareUrl, video!!.title)
    }

    fun updateVote(rating: Rating) {
        screenModelScope.launch {
            try {
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun download() {
        screenModelScope.launch {
            try {
                downloadManager
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleSubscription() {
        screenModelScope.launch {
            try {
                val channelId = video!!.author.id
                // Make request to subscribe
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setRelatedVideos() {
        relatedVideos = Pager(pagingConfig) {
            BrowsePagingSource { key ->
                if (key == null) {
                    innerTube.getNext(videoId).relatedVideos
                } else {
                    innerTube.getRelatedVideos(videoId, key)
                }
            }
        }.flow.cachedIn(screenModelScope)
    }

    /**
     * Update the player with the current video
     */
    abstract fun updatePlayer()

    abstract fun seekTo(position: Duration)

    abstract fun togglePlay()

    abstract fun toggleLoop()

    open fun setPlaybackSpeed(speed: Float) {
        this.speed = speed
    }
}