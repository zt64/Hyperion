package com.hyperion.di

import com.hyperion.domain.manager.PreferencesManager
import com.zt.innertube.network.service.InnerTubeService
import com.zt.innertube.network.service.RYDService
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    fun provideInnerTubeService(
        preferences: PreferencesManager,
        engineFactory: HttpClientEngineFactory<*>
    ) = InnerTubeService(
        engineFactory = engineFactory,
        safetyMode = false,
        visitorData = preferences.visitorData.takeUnless(String::isEmpty)
    )

    singleOf(::provideInnerTubeService)
    singleOf(::RYDService)
}