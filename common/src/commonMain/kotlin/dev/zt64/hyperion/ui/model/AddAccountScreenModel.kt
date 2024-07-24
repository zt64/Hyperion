package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.zt64.hyperion.domain.manager.AccountManager
import kotlinx.coroutines.launch

class AddAccountScreenModel(private val accountManager: AccountManager) : ScreenModel {
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

        screenModelScope.launch {
            state = try {
                State.Loaded(accountManager.getCode())
            } catch (e: Exception) {
                e.printStackTrace()
                State.Error(e)
            }
        }
    }

    fun activate() {
        // val intent = Intent(Intent.ACTION_VIEW, ACTIVATE_URL.toUri()).apply {
        //     flags = Intent.FLAG_ACTIVITY_NEW_TASK
        // }
        //
        // Toast.makeText(application, "Copied code to clipboard", Toast.LENGTH_SHORT).show()
        //
        // application.startActivity(intent)
    }

    private companion object {
        const val ACTIVATE_URL = "https://www.youtube.com/activate"
    }
}