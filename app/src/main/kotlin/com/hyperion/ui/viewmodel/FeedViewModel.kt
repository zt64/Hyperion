package com.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.hyperion.domain.manager.AccountManager
import com.hyperion.domain.manager.PreferencesManager
import com.zt.innertube.domain.model.Entity
import com.zt.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow

class FeedViewModel(
    private val preferencesManager: PreferencesManager,
    private val innerTube: InnerTubeRepository,
    val accountManager: AccountManager
) : ViewModel() {
    val items by mutableStateOf(emptyFlow<PagingData<Entity>>())
}