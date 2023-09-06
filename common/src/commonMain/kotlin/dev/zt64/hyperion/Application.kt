package dev.zt64.hyperion

import dev.zt64.hyperion.di.*
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initializeKoin(koinAppDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        koinAppDeclaration()
        modules(
            appModule,
            repositoryModule,
            httpModule,
            serviceModule,
            managerModule,
        )
    }
}