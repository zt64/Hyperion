package dev.zt64.hyperion.ui.model

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import dev.zt64.hyperion.domain.paging.BrowsePagingSource
import dev.zt64.innertube.domain.repository.InnerTubeRepository

class HomeScreenModel(
    private val innerTube: InnerTubeRepository,
    pagingConfig: PagingConfig
) : ScreenModel {
    val videos = Pager(pagingConfig) {
        BrowsePagingSource(innerTube::getRecommendations)
    }.flow.cachedIn(coroutineScope)
}