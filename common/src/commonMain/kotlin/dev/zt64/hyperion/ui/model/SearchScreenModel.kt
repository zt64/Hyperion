package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.zt64.hyperion.api.domain.repository.InnerTubeRepository
import dev.zt64.hyperion.api.model.SearchResult
import dev.zt64.hyperion.domain.manager.AccountManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class SearchScreenModel(
    private val innerTube: InnerTubeRepository,
    private val pagingConfig: PagingConfig,
    val accountManager: AccountManager
) : ScreenModel {
    var suggestions = mutableStateListOf<String>()
        private set
    var results: Flow<PagingData<SearchResult>> by mutableStateOf(flowOf(PagingData.empty()))
        private set
    var textFieldValue by mutableStateOf(TextFieldValue())
        private set
    var showFilterSheet by mutableStateOf(false)
    var searchActive by mutableStateOf(true)

    fun textFieldValueChange(value: TextFieldValue) {
        textFieldValue = value

        screenModelScope.launch {
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
        results =
            Pager(pagingConfig) {
                // BrowsePagingSource { key -> innerTube.getSearchResults(textFieldValue.text, key) }
                object : PagingSource<String, SearchResult>() {
                    override suspend fun load(params: LoadParams<String>): LoadResult<String, SearchResult> {
                        val res = innerTube.getSearchResults(textFieldValue.text, params.key)

                        return try {
                            LoadResult.Page(
                                data = res.results,
                                prevKey = res.prevToken,
                                nextKey = res.nextToken
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()

                            LoadResult.Error(e)
                        }
                    }

                    override fun getRefreshKey(state: PagingState<String, SearchResult>): String? {
                        TODO("Not yet implemented")
                    }
                }
            }.flow.cachedIn(screenModelScope)

        searchActive = false
    }

    fun clearSearch() {
        textFieldValue = TextFieldValue()
        suggestions.clear()
    }
}