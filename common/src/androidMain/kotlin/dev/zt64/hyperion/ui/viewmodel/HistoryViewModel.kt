package dev.zt64.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import dev.zt64.innertube.domain.model.DomainVideoPartial
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class HistoryViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {
    val history: Flow<PagingData<DomainVideoPartial>> by mutableStateOf(emptyFlow())
}