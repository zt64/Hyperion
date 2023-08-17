package dev.zt64.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import dev.zt64.innertube.domain.model.DomainChannelPartial
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow

class ChannelsViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {
    var channels by mutableStateOf(emptyFlow<PagingData<DomainChannelPartial>>())
        private set
}