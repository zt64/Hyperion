package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.zt64.hyperion.domain.manager.AccountManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class AddAccountScreenModel(private val accountManager: AccountManager) : ScreenModel {
    @Immutable
    sealed interface State {
        // A code is being fetched
        data object Loading : State

        // A code has been fetched and is pending activation
        data class Pending(val code: String) : State

        // The code has been activated and the account is ready to use
        data object Ready : State

        // An error occurred during the process
        data class Error(val error: Exception) : State
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    init {
        getCode()
    }

    private fun getCode() {
        state = State.Loading

        screenModelScope.launch {
            val code = try {
                accountManager.getCode()
            } catch (e: Exception) {
                state = State.Error(e)
                return@launch
            }
            state = State.Pending(code.userCode)

            // start polling every 5 seconds

            val pollingJob = launch {
                withTimeout(code.expiresIn.milliseconds) {
                    while (coroutineContext.isActive) {
                        try {
                            if (accountManager.pollRefreshToken(code.deviceCode)) {
                                // Account is ready
                                state = State.Ready
                                break
                            }
                        } catch (e: Exception) {
                            state = State.Error(e)
                        }

                        delay(code.interval.seconds)
                    }
                }

                // Code expired, reset state and fetch a new code
                getCode()
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