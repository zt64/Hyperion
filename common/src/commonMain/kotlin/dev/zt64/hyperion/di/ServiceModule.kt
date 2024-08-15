package dev.zt64.hyperion.di

import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.innertube.network.service.InnerTubeService
import dev.zt64.ryd.RydClient
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val serviceModule = module {
    fun provideInnerTubeService(
        preferences: PreferencesManager,
        engineFactory: HttpClientEngineFactory<*>
    ) = InnerTubeService(
        engineFactory = engineFactory,
        safetyMode = false,
        visitorData = preferences.visitorData
    )

    fun provideRydClient(httpClientEngineFactory: HttpClientEngineFactory<*>): RydClient {
        return RydClient(httpClientEngineFactory)
    }

    singleOf(::provideInnerTubeService)
    singleOf(::provideRydClient)
}