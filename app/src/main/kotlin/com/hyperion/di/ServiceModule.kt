package com.hyperion.di

import android.os.Build
import com.zt.innertube.network.service.InnerTubeService
import com.zt.innertube.network.service.RYDService
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    fun provideInnerTubeService(): InnerTubeService {
        return InnerTubeService(
            engineFactory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) CIO else Android,
            safetyMode = false
        )
    }

    singleOf(::provideInnerTubeService)
    singleOf(::RYDService)
}