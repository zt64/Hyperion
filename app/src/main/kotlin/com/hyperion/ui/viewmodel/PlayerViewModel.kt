package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.hyperion.model.Video
import com.hyperion.model.VideoComments
import com.hyperion.network.service.InvidiousService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    var comments = mutableStateListOf<VideoComments.Comment>()
        private set

    var relatedVideos = mutableStateListOf<Video>()
        private set

    var video by mutableStateOf<Video?>(null)

    val player = ExoPlayer.Builder(application).build().apply {
        playWhenReady = true
    }

    fun like() {

    }

    fun dislike() {

    }

    fun shareVideo() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"

            putExtra(Intent.EXTRA_TEXT, video!!.hlsUrl)
            putExtra(Intent.EXTRA_TITLE, video!!.title)
        }

        application.startActivity(Intent.createChooser(shareIntent, null).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    fun download() {

    }

    fun subscribe() {

    }

    fun viewChannel() {

    }

    fun playPause() {
        if (player.isPlaying) player.play() else player.pause()
    }

    fun fetchVideo(videoId: String) {
        viewModelScope.launch {
            video = InvidiousService.getVideo(videoId)
        }
    }
}