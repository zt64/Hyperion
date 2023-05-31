package com.hyperion.domain.manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zt.innertube.network.dto.ClientInfo
import com.zt.innertube.network.service.InnerTubeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.UUID

class AccountManager(
    private val preferencesManager: PreferencesManager,
    private val innerTubeService: InnerTubeService
) {
    private val deviceId: String = UUID.randomUUID().toString()
    private var clientInfo: ClientInfo

    var loggedIn: Boolean by mutableStateOf(false)

    init {
        runBlocking(Dispatchers.IO) {
            clientInfo = innerTubeService.getClientInfo()
        }
    }

    suspend fun getCode(): String {
        return innerTubeService.getUserCode(clientInfo.clientId, deviceId).userCode
    }
}