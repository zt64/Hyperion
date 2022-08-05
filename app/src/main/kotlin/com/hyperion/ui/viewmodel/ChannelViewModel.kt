package com.hyperion.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperion.domain.model.DomainChannel
import com.hyperion.domain.repository.InnerTubeRepository
import kotlinx.coroutines.launch

class ChannelViewModel(
    private val application: Application,
    private val repository: InnerTubeRepository
) : ViewModel() {
    sealed interface State {
        class Loaded(val channel: DomainChannel) : State
        object Loading : State
        class Error(val error: Exception) : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    fun getChannel(id: String) {
        viewModelScope.launch {
            try {
                state = State.Loading
                state = State.Loaded(repository.getChannel(id))
            } catch (e: Exception) {
                state = State.Error(e)
                e.printStackTrace()
            }
        }
    }

    fun shareChannel() {
//        Find a way to get the channel url from the state
//        val shareIntent = Intent(Intent.ACTION_SEND).apply {
//            type = "text/plain"
//
//            putExtra(Intent.EXTRA_TEXT, video!!.shareUrl)
//            putExtra(Intent.EXTRA_TITLE, video!!.title)
//        }
//
//        application.startActivity(Intent.createChooser(shareIntent, null).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        })
    }
}