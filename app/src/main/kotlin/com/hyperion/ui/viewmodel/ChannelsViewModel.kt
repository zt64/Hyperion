package com.hyperion.ui.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.zt.innertube.domain.model.DomainChannelPartial
import com.zt.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow

@Stable
class ChannelsViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {
    var channels by mutableStateOf(emptyFlow<PagingData<DomainChannelPartial>>())
        private set
}