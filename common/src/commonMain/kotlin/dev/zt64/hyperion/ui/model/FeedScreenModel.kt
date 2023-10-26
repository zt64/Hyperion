package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import cafe.adriel.voyager.core.model.ScreenModel
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.innertube.domain.model.Entity
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FeedScreenModel(
    private val preferencesManager: PreferencesManager,
    private val innerTube: InnerTubeRepository,
    val accountManager: AccountManager
) : ScreenModel {
    val items: Flow<PagingData<Entity>> by mutableStateOf(emptyFlow())
}