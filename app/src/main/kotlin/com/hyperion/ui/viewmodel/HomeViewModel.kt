package com.hyperion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.zt.innertube.domain.model.DomainVideoPartial
import com.zt.innertube.domain.repository.InnerTubeRepository

class HomeViewModel(
    private val innerTube: InnerTubeRepository
) : ViewModel() {
    val videos = Pager(PagingConfig(4)) {
        object : PagingSource<String, DomainVideoPartial>() {
            override suspend fun load(params: LoadParams<String>) = try {
                val response = innerTube.getRecommendations(params.key)

                LoadResult.Page(
                    data = response.items,
                    prevKey = null,
                    nextKey = response.continuation
                )
            } catch (e: Exception) {
                e.printStackTrace()

                LoadResult.Error(e)
            }

            override fun getRefreshKey(state: PagingState<String, DomainVideoPartial>): String? = null
        }
    }.flow.cachedIn(viewModelScope)
}