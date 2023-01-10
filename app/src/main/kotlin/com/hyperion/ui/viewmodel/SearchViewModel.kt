package com.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.zt.innertube.domain.model.Entity
import com.zt.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {
    var suggestions = mutableStateListOf<String>()
        private set
    var results by mutableStateOf(emptyFlow<PagingData<Entity>>())
        private set
    var textFieldValue by mutableStateOf(TextFieldValue())
        private set
    val focusRequester = FocusRequester()

    fun textFieldValueChange(value: TextFieldValue) {
        textFieldValue = value

        viewModelScope.launch {
            try {
                val searchSuggestions = innerTube.getSearchSuggestions(textFieldValue.text)

                suggestions.clear()
                suggestions.addAll(searchSuggestions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun replaceSuggestion(suggestion: String) {
        textFieldValueChange(
            TextFieldValue(
                text = suggestion,
                selection = TextRange(suggestion.length)
            )
        )
    }

    fun selectSuggestion(suggestion: String) {
        textFieldValue = TextFieldValue(
            text = suggestion,
            selection = TextRange(suggestion.length)
        )

        search()
    }

    fun search() {
        results = Pager(PagingConfig(4)) {
            object : PagingSource<String, Entity>() {
                override suspend fun load(params: LoadParams<String>) = try {
                    val searchResults = innerTube.getSearchResults(textFieldValue.text, params.key)

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

    fun clearSearch() {
        textFieldValue = TextFieldValue()
        suggestions.clear()
    }
}