package dev.zt64.hyperion.domain.manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.benasher44.uuid.uuid4
import dev.zt64.innertube.network.service.InnerTubeService

interface AccountManager {
    val loggedIn: Boolean

    suspend fun getCode(): String
}

internal class AccountManagerImpl(
    preferencesManager: PreferencesManager,
    private val innerTubeService: InnerTubeService
) : AccountManager {
    private val deviceId: String

    override var loggedIn: Boolean by mutableStateOf(false)
        private set

    init {
        deviceId = preferencesManager.deviceId ?: uuid4().toString().also {
            preferencesManager.deviceId = it
        }
    }

    override suspend fun getCode(): String {
        val (id, secret) = innerTubeService.getClientInfo()

        return innerTubeService.getUserCode(id, deviceId).userCode
    }
}