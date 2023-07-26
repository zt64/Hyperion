package com.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.zt.innertube.domain.model.DomainVideoPartial
import com.zt.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow

class HistoryViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {
    val history by mutableStateOf(emptyFlow<PagingData<DomainVideoPartial>>())
}