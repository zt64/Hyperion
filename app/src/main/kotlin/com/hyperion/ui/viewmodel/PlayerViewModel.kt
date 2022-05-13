package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.hyperion.domain.model.DomainStream
import com.hyperion.domain.model.DomainVideo
import com.hyperion.domain.repository.InnerTubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
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
            /* handleAudioFocus= */ true
        )
        .setRenderersFactory(
            DefaultRenderersFactory(application)
                .setEnableAudioTrackPlaybackParams(true)
                .setEnableAudioOffload(true)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        )
        .build()

    fun shareVideo() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"

//            putExtra(Intent.EXTRA_TEXT, video!!.url)
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