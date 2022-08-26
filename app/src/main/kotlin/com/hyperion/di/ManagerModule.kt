package com.hyperion.di

import android.content.Context
import com.hyperion.domain.manager.AccountManager
import com.hyperion.domain.manager.DownloadManager
import com.hyperion.domain.manager.PreferencesManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val managerModule = module {
    fun providePreferences(context: Context): PreferencesManager {
        return PreferencesManager(context.getSharedPreferences("preferences", Context.MODE_PRIVATE))
    }

    singleOf(::providePreferences)
    singleOf(::AccountManager)
    singleOf(::DownloadManager)
}