package dev.zt64.hyperion.ui.viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.domain.paging.BrowsePagingSource
import dev.zt64.innertube.domain.model.Entity
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val innerTube: InnerTubeRepository,
    private val pagingConfig: PagingConfig,
    val accountManager: AccountManager
) : ViewModel() {
    var suggestions = mutableStateListOf<String>()
        private set
    var results: Flow<PagingData<Entity>> by mutableStateOf(emptyFlow())
        private set
    var textFieldValue by mutableStateOf(TextFieldValue())
        private set
    var showFilterSheet by mutableStateOf(false)
    var searchActive by mutableStateOf(true)

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
        results = Pager(pagingConfig) {
            BrowsePagingSource { key -> innerTube.getSearchResults(textFieldValue.text, key) }
        }.flow.cachedIn(viewModelScope)

        searchActive = false
    }

    fun clearSearch() {
        textFieldValue = TextFieldValue()
        suggestions.clear()
    }
}