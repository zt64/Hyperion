package dev.zt64.hyperion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.zt64.hyperion.domain.paging.BrowsePagingSource
import dev.zt64.innertube.domain.repository.InnerTubeRepository

class HomeViewModel(
    private val innerTube: InnerTubeRepository,
    pagingConfig: PagingConfig
) : ViewModel() {
    val videos = Pager(pagingConfig) {
        BrowsePagingSource(innerTube::getRecommendations)
    }.flow.cachedIn(viewModelScope)
}