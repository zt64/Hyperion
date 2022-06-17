package com.hyperion

import android.app.Application
import com.hyperion.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HyperionApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HyperionApplication)
            modules(httpModule, serviceModule, repositoryModule, viewModelModule, managerModule)
        }
    }
}