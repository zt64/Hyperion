package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.*
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.*
import com.hyperion.domain.model.DomainStream
import com.hyperion.domain.model.DomainVideo
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.domain.repository.InnerTubeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class PlayerViewModel(
    private val application: Application,
    private val repository: InnerTubeRepository
) : ViewModel() {
    sealed interface State {
        object Loaded : State
        object Loading : State
        object Error : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    var video by mutableStateOf<DomainVideo?>(null)
        private set

    var stream by mutableStateOf<DomainStream?>(null)
        private set

    var isFullscreen by mutableStateOf(false)
        private set

    var showFullDescription by mutableStateOf(false)
        private set

    var showControls by mutableStateOf(false)
        private set

    var showMoreOptions by mutableStateOf(false)
        private set

    private val listener: Player.Listener = object : Player.Listener {
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            this@PlayerViewModel.playWhenReady = playWhenReady
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            this@PlayerViewModel.isPlaying = isPlaying
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            this@PlayerViewModel.isLoading = isLoading
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            this@PlayerViewModel.playbackState = playbackState
        }

        override fun onPlayerError(error: PlaybackException) {
            state = State.Error
        }
    }

    val player = ExoPlayer.Builder(application)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build(),
            /* handleAudioFocus = */ true
        )
        .setRenderersFactory(
            DefaultRenderersFactory(application)
                .setEnableAudioTrackPlaybackParams(true)
                .setEnableAudioOffload(true)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        )
        .setSeekBackIncrementMs(15000)
        .setSeekForwardIncrementMs(15000)
        .build()
        .apply {
            playWhenReady = true

            addListener(listener)
        }

    var playWhenReady: Boolean by mutableStateOf(player.playWhenReady)
        private set

    var isPlaying: Boolean by mutableStateOf(player.isPlaying)
        private set

    var isLoading: Boolean by mutableStateOf(player.isLoading)
        private set

    @get:Player.State
    var playbackState: Int by mutableStateOf(player.playbackState)
        private set

    var duration: Duration by mutableStateOf(Duration.ZERO)
        private set

    var position: Duration by mutableStateOf(Duration.ZERO)
        private set

    val relatedVideos = Pager(PagingConfig(4)) {
        object : PagingSource<String, DomainVideoPartial>() {
            override suspend fun load(params: LoadParams<String>): LoadResult<String, DomainVideoPartial> {
                return try {
                    val relatedVideosResponse = if (params.key == null) {
                        repository.getNext(video!!.id).relatedVideos
                    } else {
                        repository.getRelatedVideos(video!!.id, params.key!!)
                    }

                    LoadResult.Page(
                        data = relatedVideosResponse.videos,
                        prevKey = null,
                        nextKey = relatedVideosResponse.continuation
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<String, DomainVideoPartial>): String? = null
        }
    }.flow.cachedIn(viewModelScope)

    private val job = viewModelScope.launch {
        while (true) {
            duration = player.duration.takeUnless { it == C.TIME_UNSET }?.milliseconds ?: Duration.ZERO
            position = player.currentPosition.milliseconds
            delay(500)
        }
    }

//    TODO: Add some form of history for next/previous video navigation
//    val videoStack = mutableStateListOf<String>()

    override fun onCleared() {
        job.cancel()
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
    fun seekTo(milliseconds: Float) = player.seekTo(milliseconds.toLong())

    fun toggleDescription() {
        showFullDescription = !showFullDescription
    }

    fun toggleControls() {
        showControls = !showControls
    }

    fun toggleMoreOptions() {
        showMoreOptions = !showMoreOptions
    }

    fun togglePlayPause() {
        isPlaying = !isPlaying
        player.playWhenReady = !player.playWhenReady
    }

    fun toggleFullscreen() = if (isFullscreen) exitFullscreen() else enterFullscreen()

    // TODO: Use enum for like & dislike
    fun updateVote(like: Boolean) {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun download() {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadVideo(id: String) {
        viewModelScope.launch {
            try {
                state = State.Loading
                video = repository.getVideo(id)
                stream = video!!.streams.last()

                val mediaItem = MediaItem.fromUri(stream!!.url)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()

                state = State.Loaded
            } catch (e: Exception) {
                e.printStackTrace()
                state = State.Error
            }
        }
    }

    fun updateSubscription(isSubscribed: Boolean) {
        viewModelScope.launch {
            try {
                val channelId = video!!.author.id
                // Make request to subscribe
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun enterFullscreen() {
        isFullscreen = true

    }

    fun exitFullscreen() {
        isFullscreen = false

    }

    fun showComments() {

    }
}