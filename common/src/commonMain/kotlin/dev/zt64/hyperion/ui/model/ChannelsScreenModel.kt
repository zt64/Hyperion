package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import cafe.adriel.voyager.core.model.ScreenModel
import dev.zt64.hyperion.api.domain.model.DomainChannelPartial
import dev.zt64.hyperion.api.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class ChannelsScreenModel(innerTube: InnerTubeRepository) : ScreenModel {
    var channels: Flow<PagingData<DomainChannelPartial>> by mutableStateOf(emptyFlow())
        private set
}