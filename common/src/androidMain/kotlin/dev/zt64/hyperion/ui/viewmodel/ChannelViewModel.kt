package dev.zt64.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.domain.model.channel.VideoSort
import dev.zt64.innertube.domain.model.DomainChannel
import dev.zt64.innertube.domain.model.DomainVideoPartial
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import dev.zt64.innertube.network.dto.browse.ChannelTab
import kotlinx.coroutines.launch

class ChannelViewModel(
    private val application: Application,
    private val innerTube: InnerTubeRepository,
    val accountManager: AccountManager,
    pagingConfig: PagingConfig,
    channelId: String
) : ViewModel() {
    @Immutable
    sealed interface State {
        data object Loading : State
        data class Loaded(val channel: DomainChannel) : State
        data class Error(val error: Exception) : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set
    var id by mutableStateOf("")
        private set
    var tab by mutableStateOf(ChannelTab.HOME)
        private set
    var videoSort by mutableStateOf(VideoSort.RECENTLY_UPLOADED)
        private set
    var videos = Pager(pagingConfig) {
        object : PagingSource<String, DomainVideoPartial>() {
            override suspend fun load(params: LoadParams<String>): LoadResult<String, DomainVideoPartial> {
                TODO("Not yet implemented")
            }

            override fun getRefreshKey(state: PagingState<String, DomainVideoPartial>): String? = null
        }
    }.flow.cachedIn(viewModelScope)

    var channels = Pager(pagingConfig) {
        object : PagingSource<String, DomainVideoPartial>() {
            override suspend fun load(params: LoadParams<String>): LoadResult<String, DomainVideoPartial> {
                TODO("Not yet implemented")
            }

            override fun getRefreshKey(state: PagingState<String, DomainVideoPartial>): String? = null
        }
    }.flow.cachedIn(viewModelScope)

    init {
        state = State.Loading
        id = channelId

        viewModelScope.launch {
            state = try {
                State.Loaded(innerTube.getChannel(id))
            } catch (e: Exception) {
                e.printStackTrace()
                State.Error(e)
            }
        }
    }

    fun getChannelTab(tab: ChannelTab) {
        this.tab = tab

        viewModelScope.launch {
            innerTube.getChannel(id, tab)
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