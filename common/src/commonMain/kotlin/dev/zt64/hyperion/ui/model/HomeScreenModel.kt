package dev.zt64.hyperion.ui.model

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.zt64.hyperion.api.domain.repository.InnerTubeRepository
import dev.zt64.hyperion.domain.paging.BrowsePagingSource

class HomeScreenModel(private val innerTube: InnerTubeRepository, pagingConfig: PagingConfig) : ScreenModel {
    val videos = Pager(pagingConfig) {
        // object : PagingSource<String> {
        //
        // }

        BrowsePagingSource(innerTube::getRecommendations)
    }.flow.cachedIn(screenModelScope)

    val filters = listOf(
        "All"
    )
}