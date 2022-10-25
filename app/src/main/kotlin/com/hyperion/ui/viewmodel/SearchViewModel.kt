package com.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.hyperion.domain.model.Entity
import com.hyperion.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: InnerTubeRepository) : ViewModel() {
    var search by mutableStateOf("")
        private set
    var suggestions = mutableStateListOf<String>()
        private set
    var results by mutableStateOf(emptyFlow<PagingData<Entity>>())
        private set
    val focusRequester = FocusRequester()

    fun getSuggestions(query: String) {
        search = query

        viewModelScope.launch {
            try {
                val searchSuggestions = repository.getSearchSuggestions(query)

                suggestions.clear()
                suggestions.addAll(searchSuggestions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getResults() {
        results = Pager(PagingConfig(4)) {
            object : PagingSource<String, Entity>() {
                override suspend fun load(params: LoadParams<String>): LoadResult<String, Entity> = try {
                    val searchResults = repository.getSearchResults(search, params.key)

                    LoadResult.Page(
                        data = searchResults.items,
                        prevKey = null,
                        nextKey = searchResults.continuation
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    LoadResult.Error(e)
                }

                override fun getRefreshKey(state: PagingState<String, Entity>): String? = null
            }
        }.flow.cachedIn(viewModelScope)
    }

    fun search(query: String) {
        search = query

        getResults()
    }
}