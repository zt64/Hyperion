package dev.zt64.hyperion.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dev.zt64.hyperion.domain.paging.BrowsePagingSource
import dev.zt64.innertube.domain.model.DomainVideoPartial
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class TagViewModel(
    private val innerTube: InnerTubeRepository,
    private val pagingConfig: PagingConfig,
    tag: String
) : ViewModel() {
    @Immutable
    sealed interface State {
        data object Loading : State
        data object Loaded : State
        data class Error(val exception: Exception) : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set
    var tag by mutableStateOf<String?>(null)
        private set
    var subtitle by mutableStateOf<String?>(null)
        private set
    var videos by mutableStateOf(emptyFlow<PagingData<DomainVideoPartial>>())
        private set

    init {
        state = State.Loading

        viewModelScope.launch {
            state = try {
                val response = innerTube.getTag(tag)

                this@TagViewModel.tag = response.name
                subtitle = response.subtitle

                videos = Pager(pagingConfig) {
                    BrowsePagingSource { key ->
                        if (key == null) response else innerTube.getTagContinuation(key)
                    }
                }.flow.cachedIn(viewModelScope)

                State.Loaded
            } catch (e: Exception) {
                e.printStackTrace()
                State.Error(e)
            }
        }
    }
}