package com.hyperion.domain.manager

import com.zt.innertube.network.service.InnerTubeService

class AccountManager(
    private val preferencesManager: PreferencesManager,
    private val innerTubeService: InnerTubeService
)