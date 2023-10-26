package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.*
import androidx.paging.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import dev.zt64.hyperion.domain.manager.*
import dev.zt64.hyperion.domain.model.Rating
import dev.zt64.hyperion.domain.paging.BrowsePagingSource
import dev.zt64.innertube.domain.model.*
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.Flow
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
    Ended
}

interface IPlayerScreenModel : ScreenModel {
    val state: PlayerState
    val video: DomainVideo?
    val isFullscreen: Boolean
    val isPlaying: Boolean
    val isLoading: Boolean
    val playbackState: PlaybackState
    val showFullDescription: Boolean
    val showControls: Boolean
    val showQualityPicker: Boolean
    val showDownloadDialog: Boolean
    val showCaptions: Boolean
    val showCommentsSheet: Boolean
    val duration: Duration
    val position: Duration
    val speed: Float
    val repeatMode: Int

    val relatedVideos: Flow<PagingData<DomainVideoPartial>>
    val comments: Flow<PagingData<DomainComment>>

    fun skipForward()
    fun skipBackward()
    fun skipNext()
    fun skipPrevious()
    fun seekTo(position: Duration)

    fun togglePlay()
    fun toggleFullscreen()
    fun toggleCaptions()
    fun toggleDescription()
    fun toggleControls()
    fun toggleLoop()

    fun showComments()
    fun hideComments()
    fun showOptions()
    fun hideOptions()
    fun showDownloadDialog()
    fun hideDownloadDialog()
    fun shareVideo()
    fun setPlaybackSpeed(speed: Float)
    fun updateVote(rating: Rating)
    fun selectFormat(format: DomainFormat.Video)
    fun download()
    fun toggleSubscription()
}

@Immutable
sealed interface PlayerState {
    data object Loaded : PlayerState
    data object Loading : PlayerState
    data class Error(val exception: Exception) : PlayerState
}

expect class PlayerScreenModel internal constructor(videoId: String) : AbstractPlayerScreenModel

/**
 * Common screen model for the player screen that handles logic
 *
 * @property videoId
 */
abstract class AbstractPlayerScreenModel(private val videoId: String) : IPlayerScreenModel, KoinComponent {
    val accountManager = get<AccountManager>()
    val preferences = get<PreferencesManager>()

    private val downloadManager = get<DownloadManager>()
    private val innerTube = get<InnerTubeRepository>()
    private val shareManager = get<ShareManager>()
    private val pagingConfig = get<PagingConfig>()

    final override var state by mutableStateOf<PlayerState>(PlayerState.Loading)
        protected set
    final override var video by mutableStateOf<DomainVideo?>(null)
        protected set

    var videoFormats = mutableStateListOf<DomainFormat.Video>()
        private set
    var videoFormat: DomainFormat.Video? = null
        private set

    final override var isFullscreen by mutableStateOf(false)
        private set
    final override var showFullDescription by mutableStateOf(false)
        private set
    final override var showControls by mutableStateOf(false)
    final override var showQualityPicker by mutableStateOf(false)
        protected set
    final override var showDownloadDialog by mutableStateOf(false)
        private set
    final override var showCaptions by mutableStateOf(false)
        private set
    final override var showCommentsSheet by mutableStateOf(false)
        private set
    final override var duration by mutableStateOf(Duration.ZERO)
        protected set
    final override var position by mutableStateOf(Duration.ZERO)
        protected set
    final override var speed by mutableFloatStateOf(1f)
        private set

    final override var relatedVideos = emptyFlow<PagingData<DomainVideoPartial>>()
        private set
    final override var comments = emptyFlow<PagingData<DomainComment>>()
        private set

    init {
        loadVideo(videoId)
    }

    fun loadVideo(id: String) {
        coroutineScope.launch {
            try {
                state = PlayerState.Loading

                video = innerTube.getVideo(id)

                state = PlayerState.Loaded

                videoFormats.clear()
                videoFormats.addAll(video!!.formats.filterIsInstance<DomainFormat.Video>())
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

    abstract fun updatePlayer()

    override fun selectFormat(format: DomainFormat.Video) {
        updatePlayer()
    }

    final override fun showComments() {
        showCommentsSheet = true
    }

    final override fun hideComments() {
        showCommentsSheet = false
    }

    final override fun showOptions() {
        showQualityPicker = true
    }

    final override fun hideOptions() {
        showQualityPicker = false
    }

    final override fun showDownloadDialog() {
        showDownloadDialog = true
    }

    final override fun hideDownloadDialog() {
        showDownloadDialog = false
    }

    final override fun toggleDescription() {
        showFullDescription = !showFullDescription
    }

    final override fun toggleControls() {
        showControls = !showControls
    }

    final override fun toggleFullscreen() {
        isFullscreen = !isFullscreen
    }

    override fun toggleCaptions() {
        showCaptions = !showCaptions
    }

    final override fun shareVideo() {
        shareManager.share(video!!.shareUrl, video!!.title)
    }

    override fun setPlaybackSpeed(speed: Float) {
        this.speed = speed
    }

    final override fun updateVote(rating: Rating) {
        coroutineScope.launch {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    final override fun download() {
        coroutineScope.launch {
            try {
                downloadManager
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    final override fun toggleSubscription() {
        coroutineScope.launch {
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
        }.flow.cachedIn(coroutineScope)
    }
}