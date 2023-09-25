package dev.zt64.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.innertube.domain.model.Entity
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FeedViewModel(
    private val preferencesManager: PreferencesManager,
    private val innerTube: InnerTubeRepository,
    val accountManager: AccountManager
) : ViewModel() {
    val items: Flow<PagingData<Entity>> by mutableStateOf(emptyFlow())
}