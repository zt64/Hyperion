package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperion.domain.manager.AccountManager
import kotlinx.coroutines.launch

@Stable
class AddAccountViewModel(
    private val application: Application,
    private val accountManager: AccountManager
) : ViewModel() {
    @Immutable
    sealed interface State {
        data object Loading : State
        data class Loaded(val code: String) : State
        data class Error(val error: Exception) : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    init {
        state = State.Loading

        viewModelScope.launch {
            state = try {
                State.Loaded(accountManager.getCode())
            } catch (e: Exception) {
                e.printStackTrace()
                State.Error(e)
            }
        }
    }

    fun activate() {
        val intent = Intent(Intent.ACTION_VIEW, ACTIVATE_URL.toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        Toast.makeText(application, "Copied code to clipboard", Toast.LENGTH_SHORT).show()

        application.startActivity(intent)
    }

    private companion object {
        const val ACTIVATE_URL = "https://www.youtube.com/activate"
    }
}