package dev.zt64.hyperion.di

import dev.zt64.hyperion.domain.manager.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val managerModule = module {
    singleOf(::PreferencesManagerImpl) bind PreferencesManager::class
    singleOf(::AccountManagerImpl) bind AccountManager::class
    singleOf(::DownloadManager)
}