package dev.zt64.hyperion.di

import dev.zt64.hyperion.api.network.service.InnerTubeService
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.ryd.RydClient
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val serviceModule = module {
    fun provideInnerTubeService(preferences: PreferencesManager, engineFactory: HttpClientEngineFactory<*>): InnerTubeService {
        val visitorData = preferences.visitorData ?: InnerTubeService.generateVisitorData().also {
            preferences.visitorData = it
        }

        return InnerTubeService(
            engineFactory = engineFactory,
            safetyMode = false,
            visitorData = visitorData
        )
    }

    fun provideRydClient(httpClientEngineFactory: HttpClientEngineFactory<*>): RydClient {
        return RydClient(httpClientEngineFactory)
    }

    singleOf(::provideInnerTubeService)
    singleOf(::provideRydClient)
}