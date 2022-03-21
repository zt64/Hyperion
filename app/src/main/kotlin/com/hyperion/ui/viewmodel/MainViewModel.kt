package com.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperion.model.TrendingVideo
import com.hyperion.network.service.InvidiousService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    sealed class State {
        class Loaded(val videos: List<TrendingVideo>) : State()
        object Loading : State()

        val isLoading get() = this is Loading
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    init {
        fetchTrending()
    }

    fun fetchTrending() {
        state = State.Loading

        viewModelScope.launch {
            val trendingVideos = InvidiousService.getTrending()

            state = State.Loaded(trendingVideos)
        }
    }
}