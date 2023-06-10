package com.hyperion.domain.manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zt.innertube.network.service.InnerTubeService
import java.util.UUID

class AccountManager(
    preferencesManager: PreferencesManager,
    private val innerTubeService: InnerTubeService
) {
    private val deviceId: String

    var loggedIn: Boolean by mutableStateOf(false)

    init {
        deviceId = preferencesManager.deviceId.takeUnless { it.isEmpty() }
            ?: UUID.randomUUID().toString().also { preferencesManager.deviceId = it }
    }

    suspend fun getCode(): String {
        TODO()
    }
}