package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import cafe.adriel.voyager.core.model.ScreenModel
import dev.zt64.innertube.domain.model.DomainVideoPartial
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class HistoryScreenModel(
    private val innerTube: InnerTubeRepository
) : ScreenModel {
    val history: Flow<PagingData<DomainVideoPartial>> by mutableStateOf(emptyFlow())
}