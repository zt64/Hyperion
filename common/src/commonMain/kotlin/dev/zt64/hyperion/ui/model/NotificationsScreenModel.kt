package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import cafe.adriel.voyager.core.model.ScreenModel
import dev.zt64.hyperion.api.domain.model.Notification
import dev.zt64.hyperion.api.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class NotificationsScreenModel(private val innerTube: InnerTubeRepository) : ScreenModel {
    val notifications: Flow<PagingData<Notification>> by mutableStateOf(emptyFlow())
}