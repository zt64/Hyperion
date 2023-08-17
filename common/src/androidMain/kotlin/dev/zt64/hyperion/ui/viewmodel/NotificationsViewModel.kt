package dev.zt64.hyperion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import dev.zt64.innertube.domain.model.Notification
import dev.zt64.innertube.domain.repository.InnerTubeRepository
import kotlinx.coroutines.flow.emptyFlow

class NotificationsViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {
    val notifications by mutableStateOf(emptyFlow<PagingData<Notification>>())
}