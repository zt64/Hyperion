package com.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.domain.repository.InnerTubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: InnerTubeRepository
) : ViewModel() {
    sealed class State {
        object Loaded : State()
        object Loading : State()
        object Error : State()

        val isLoading get() = this is Loading
    }

    var state by mutableStateOf<State>(State.Loading)
        private set
    var search by mutableStateOf("")
        private set
    var suggestions = mutableStateListOf<String>()
        private  set
    var results = mutableStateListOf<DomainVideoPartial>()
        private set

    init {
        fetchSuggestions("")
    }

    fun fetchSuggestions(query: String) {
        search = query

        viewModelScope.launch(Dispatchers.IO) {
            val searchSuggestions = repository.getSuggestions(query)

            suggestions.clear()
            suggestions.addAll(searchSuggestions)
        }
    }

    fun fetchResults() {

        viewModelScope.launch {
            try {
                repository.search(search)
                results.clear()
            } catch (e: Exception) {
                state = State.Error
                e.printStackTrace()
            }
        }
    }
}