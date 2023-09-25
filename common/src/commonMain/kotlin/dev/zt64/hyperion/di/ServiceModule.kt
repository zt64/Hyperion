package dev.zt64.hyperion.di

import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.innertube.network.service.InnerTubeService
import dev.zt64.innertube.network.service.RYDService
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

    singleOf(::provideInnerTubeService)
    singleOf(::RYDService)
}