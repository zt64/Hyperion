package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.*
import androidx.paging.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import dev.zt64.hyperion.domain.paging.BrowsePagingSource
import dev.zt64.innertube.domain.model.DomainVideoPartial
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class TagScreenModel(
    private val innerTube: InnerTubeRepository,
    private val pagingConfig: PagingConfig,
    tag: String
) : ScreenModel {
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

        coroutineScope.launch {
            state = try {
                val response = innerTube.getTag(tag)

                this@TagScreenModel.tag = response.name
                subtitle = response.subtitle

                videos = Pager(pagingConfig) {
                    BrowsePagingSource { key ->
                        if (key == null) response else innerTube.getTagContinuation(key)
                    }
                }.flow.cachedIn(coroutineScope)

                State.Loaded
            } catch (e: Exception) {
                e.printStackTrace()
                State.Error(e)
            }
        }
    }
}