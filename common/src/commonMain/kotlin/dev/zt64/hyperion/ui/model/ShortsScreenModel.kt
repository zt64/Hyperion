package dev.zt64.hyperion.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import cafe.adriel.voyager.core.model.ScreenModel
import dev.zt64.innertube.domain.model.DomainVideoPartial
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class ShortsScreenModel : ScreenModel {
    val shorts: Flow<PagingData<DomainVideoPartial>> by mutableStateOf(emptyFlow())

    fun showMenu() {

    }
}