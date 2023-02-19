package com.hyperion.domain.manager

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

    init {
        runBlocking(Dispatchers.IO) {
            clientInfo = innerTubeService.getClientInfo()
        }
    }

    suspend fun getCode(): String {
        return innerTubeService.getUserCode(clientInfo.clientId, deviceId).userCode
    }
}