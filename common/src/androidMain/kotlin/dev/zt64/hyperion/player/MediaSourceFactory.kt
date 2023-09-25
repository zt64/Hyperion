package dev.zt64.hyperion.player

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import dev.zt64.innertube.domain.repository.InnerTubeRepository

@UnstableApi
fun buildMediaSourceFactory(cache: Cache, innerTube: InnerTubeRepository): DashMediaSourceFactory {
    return DashMediaSourceFactory(
        dataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setCacheWriteDataSinkFactory(CacheDataSink.Factory().setCache(cache))
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR),
        repository = innerTube
    )
}