package com.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperion.domain.model.DomainChannel
import com.hyperion.domain.repository.InnerTubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val repository: InnerTubeRepository
) : ViewModel() {
    sealed class State {
        class Loaded(val channel: DomainChannel) : State()
        object Loading : State()
        object Error : State()

        val isLoading get() = this is Loading
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    fun getChannel(id: String) {
        viewModelScope.launch {
            try {
                state = State.Loaded(repository.getChannel(id))
            } catch (e: Exception) {
                state = State.Error
                e.printStackTrace()
            }
        }
    }
}