package dev.zt64.hyperion.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.zt64.innertube.domain.model.DomainBrowse

class BrowsePagingSource<T : Any>(
    private val getter: suspend (key: String?) -> DomainBrowse<T>
) : PagingSource<String, T>() {
    override suspend fun load(params: LoadParams<String>) = try {
        val response = getter(params.key)

        LoadResult.Page(
            data = response.items,
            prevKey = null,
            nextKey = response.continuation
        )
    } catch (e: Exception) {
        e.printStackTrace()

        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<String, T>): String? = null
}