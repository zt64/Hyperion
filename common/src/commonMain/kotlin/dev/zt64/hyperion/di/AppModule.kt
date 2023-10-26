package dev.zt64.hyperion.di

import androidx.paging.PagingConfig
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    includes(
        repositoryModule,
        httpModule,
        serviceModule,
        managerModule,
        screenModelModule
    )

    fun providePagingConfig(): PagingConfig = PagingConfig(
        pageSize = 20,
        enablePlaceholders = false
    )

    singleOf(::providePagingConfig)
}