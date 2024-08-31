package dev.zt64.hyperion.domain.manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.benasher44.uuid.uuid4
import dev.zt64.hyperion.api.network.dto.ClientInfo
import dev.zt64.hyperion.api.network.dto.auth.AccessToken
import dev.zt64.hyperion.api.network.dto.auth.UserCode
import dev.zt64.hyperion.api.network.service.InnerTubeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.TimeSource

interface AccountManager {
    /** Whether the user is currently logged in */
    val loggedIn: Boolean

    suspend fun getCode(): UserCode

    /** Check if the user has authorized the app */
    suspend fun pollRefreshToken(deviceCode: String): Boolean
}

/**
 * AccountManager for handling multiple accounts. This class is responsible for handling the
 * sign-in process and refreshing the access token. It is also responsible for storing the
 * active account.
 */
internal class AccountManagerImpl(preferencesManager: PreferencesManager, private val innerTubeService: InnerTubeService) : AccountManager {
    private val deviceId: String
    private lateinit var clientInfo: ClientInfo

    override var loggedIn: Boolean by mutableStateOf(false)
        private set

    private val _tokenFlow = MutableStateFlow<UserCode?>(null)
    val tokenFlow: StateFlow<UserCode?> = _tokenFlow.asStateFlow()
    private var tokenTimestamp: Long = 0

    init {
        deviceId = preferencesManager.deviceId ?: uuid4().toString().also {
            preferencesManager.deviceId = it
        }
    }

    override suspend fun getCode(): UserCode {
        if (!::clientInfo.isInitialized) {
            clientInfo = innerTubeService.getClientInfo()
        }

        val now = TimeSource.Monotonic.markNow()
        val currentToken = _tokenFlow.value

        if (currentToken == null || now.elapsedNow().inWholeSeconds > currentToken.expiresIn) {
            val newToken = fetchNewToken()
            _tokenFlow.value = newToken
            tokenTimestamp = now.elapsedNow().inWholeMilliseconds
        }

        return _tokenFlow.value!!
    }

    private suspend fun fetchNewToken(): UserCode {
        println("Fetching new token")
        return innerTubeService.getUserCode(clientInfo.clientId, deviceId)
    }

    override suspend fun pollRefreshToken(deviceCode: String): Boolean {
        val token = innerTubeService.getAccessToken(
            deviceCode,
            clientInfo.clientId,
            clientInfo.clientSecret
        )

        return when (token) {
            is AccessToken.Authorized -> true
            is AccessToken.Error -> {
                if (token !is AccessToken.Error.AuthPending) {
                    // Fetch a new token
                    // _tokenFlow.value = null
                }

                false
            }
        }
    }
}