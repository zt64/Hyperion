package com.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperion.domain.model.DomainChannel
import com.hyperion.domain.repository.InnerTubeRepository
import kotlinx.coroutines.launch

class ChannelViewModel(
    private val repository: InnerTubeRepository
) : ViewModel() {
    sealed class State {
        class Loaded(val channel: DomainChannel) : State()
        object Loading : State()
        class Error(val error: Exception) : State()
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    fun getChannel(id: String) {
        viewModelScope.launch {
            try {
                state = State.Loaded(repository.getChannel(id))
            } catch (e: Exception) {
                state = State.Error(e)
                e.printStackTrace()
            }
        }
    }
}