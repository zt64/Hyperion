package com.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.zt.innertube.domain.model.DomainVideoPartial
import com.zt.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class TagViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {
    sealed interface State {
        object Loading : State
        object Loaded : State
        class Error(val exception: Exception) : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set
    var tag by mutableStateOf<String?>(null)
        private set
    var subtitle by mutableStateOf<String?>(null)
        private set
    val avatars = mutableStateListOf<String>()
    var videos by mutableStateOf(emptyFlow<PagingData<DomainVideoPartial>>())
        private set

    fun getTag(tagQuery: String) {
        state = State.Loading

        viewModelScope.launch {
            state = try {
                val response = innerTube.getTag(tagQuery)

                tag = response.name
                subtitle = response.subtitle
                avatars.clear()
                avatars.addAll(response.avatars)

                videos = Pager(PagingConfig(4)) {
                    object : PagingSource<String, DomainVideoPartial>() {
                        override suspend fun load(params: LoadParams<String>) = try {
                            val content = if (params.key == null) {
                                response
                            } else {
                                innerTube.getTagContinuation(params.key!!)
                            }

                            LoadResult.Page(
                                data = content.items,
                                prevKey = null,
                                nextKey = content.continuation
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()

                            LoadResult.Error(e)
                        }

                        override fun getRefreshKey(state: PagingState<String, DomainVideoPartial>): String? = null
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