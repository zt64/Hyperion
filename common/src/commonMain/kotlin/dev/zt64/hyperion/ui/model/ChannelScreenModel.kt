package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.*
import androidx.paging.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.domain.manager.ShareManager
import dev.zt64.hyperion.domain.model.channel.VideoSort
import dev.zt64.innertube.domain.model.DomainChannel
import dev.zt64.innertube.domain.model.DomainVideoPartial
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import dev.zt64.innertube.network.dto.browse.ChannelTab
import kotlinx.coroutines.launch

class ChannelScreenModel(
    private val innerTube: InnerTubeRepository,
    private val shareManager: ShareManager,
    val accountManager: AccountManager,
    pagingConfig: PagingConfig,
    channelId: String
) : ScreenModel {
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
    }.flow.cachedIn(coroutineScope)

    var channels = Pager(pagingConfig) {
        object : PagingSource<String, DomainVideoPartial>() {
            override suspend fun load(params: LoadParams<String>): LoadResult<String, DomainVideoPartial> {
                TODO("Not yet implemented")
            }

            override fun getRefreshKey(state: PagingState<String, DomainVideoPartial>): String? = null
        }
    }.flow.cachedIn(coroutineScope)

    init {
        state = State.Loading
        id = channelId

        coroutineScope.launch {
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

        coroutineScope.launch {
            innerTube.getChannel(id, tab)
        }
    }

    fun subscribe() {

    }
}