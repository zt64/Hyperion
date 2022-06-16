package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.*
import com.hyperion.domain.model.DomainStream
import com.hyperion.domain.model.DomainVideo
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.domain.repository.InnerTubeRepository
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val application: Application,
    private val repository: InnerTubeRepository
) : ViewModel() {
    sealed class State {
        object Loaded : State()
        object Loading : State()
        object Error : State()
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    var video by mutableStateOf<DomainVideo?>(null)
        private set

    var stream by mutableStateOf<DomainStream.Video?>(null)
        private set

    val player = ExoPlayer.Builder(application)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build(),
            /* handleAudioFocus = */ true
        )
        .setRenderersFactory(
            DefaultRenderersFactory(application)
                .setEnableAudioTrackPlaybackParams(true)
                .setEnableAudioOffload(true)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        )
        .build()

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

    fun playPause() {
        player.playWhenReady = !player.playWhenReady
    }

    fun getVideo(id: String) {
        viewModelScope.launch {
            try {
                video = repository.getVideo(id)
                state = State.Loaded
            } catch (e: Exception) {
                e.printStackTrace()
                state = State.Error
            }
        }
    }

    // TODO
    fun subscribe() {

    }
}