package com.hyperion.ui.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.hyperion.domain.paging.BrowsePagingSource
import com.zt.innertube.domain.repository.InnerTubeRepository

@Stable
class HomeViewModel(
    private val innerTube: InnerTubeRepository,
    pagingConfig: PagingConfig
) : ViewModel() {
    val videos = Pager(pagingConfig) {
        BrowsePagingSource(innerTube::getRecommendations)
    }.flow.cachedIn(viewModelScope)
}