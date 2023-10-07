package dev.zt64.hyperion.di

import org.koin.core.KoinApplication

fun KoinApplication.commonModules() {
    modules(
        appModule,
        repositoryModule,
        httpModule,
        serviceModule,
        managerModule,
    )
}