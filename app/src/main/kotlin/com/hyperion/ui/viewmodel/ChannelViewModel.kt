package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperion.domain.manager.AccountManager
import com.hyperion.domain.model.channel.ChannelTab
import com.hyperion.domain.model.channel.VideoSort
import com.zt.innertube.domain.model.DomainChannel
import com.zt.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.launch

class ChannelViewModel(
    private val application: Application,
    private val innerTube: InnerTubeRepository,
    private val accountManager: AccountManager
) : ViewModel() {
    @Immutable
    sealed interface State {
        class Loaded(val channel: DomainChannel) : State
        object Loading : State
        class Error(val error: Exception) : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    var tab by mutableStateOf(ChannelTab.HOME)
    var videoSort by mutableStateOf(VideoSort.RECENTLY_UPLOADED)

    fun getChannel(id: String) {
        state = State.Loading

        viewModelScope.launch {
            state = try {
                State.Loaded(innerTube.getChannel(id))
            } catch (e: Exception) {
                e.printStackTrace()
                State.Error(e)
            }
        }
    }

    fun shareChannel() {
        val channel = (state as State.Loaded).channel

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"

            putExtra(Intent.EXTRA_TEXT, channel.shareUrl)
            putExtra(Intent.EXTRA_TITLE, channel.name)
        }

        application.startActivity(Intent.createChooser(shareIntent, null).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    fun subscribe() {

    }
}