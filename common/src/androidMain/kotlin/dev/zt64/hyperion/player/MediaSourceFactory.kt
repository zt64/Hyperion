package dev.zt64.hyperion.player

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import dev.zt64.hyperion.api.domain.repository.InnerTubeRepository
import okhttp3.OkHttpClient

@UnstableApi
internal fun buildMediaSourceFactory(cache: Cache, innerTube: InnerTubeRepository): DashMediaSourceFactory = DashMediaSourceFactory(
    dataSourceFactory = CacheDataSource
        .Factory()
        .setCache(cache)
        .setCacheWriteDataSinkFactory(CacheDataSink.Factory().setCache(cache))
        .setUpstreamDataSourceFactory(OkHttpDataSource.Factory(OkHttpClient()))
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR),
    repository = innerTube
)