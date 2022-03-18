package com.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperion.model.Video
import com.hyperion.model.VideoComments
import kotlinx.coroutines.launch

class PlayerViewModel(
    video: Video
) : ViewModel() {
    var video by mutableStateOf(video)
        private set

    var comments = mutableStateListOf<VideoComments.Comment>()
        private set

    fun fetchComments(continuation: String?) {
        viewModelScope.launch {
//            comments = InvidiousApi.getComments(video.videoId, continuation).comments
        }
    }
}