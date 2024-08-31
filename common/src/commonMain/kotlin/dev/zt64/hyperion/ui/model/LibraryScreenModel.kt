package dev.zt64.hyperion.ui.model

import cafe.adriel.voyager.core.model.ScreenModel
import dev.zt64.hyperion.api.domain.repository.InnerTubeRepository
import dev.zt64.hyperion.domain.manager.AccountManager

class LibraryScreenModel(private val innerTube: InnerTubeRepository, private val accountManager: AccountManager) : ScreenModel {
    val loggedIn
        get() = accountManager.loggedIn
}