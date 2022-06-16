package com.hyperion

import android.app.Application
import com.hyperion.di.httpModule
import com.hyperion.di.repositoryModule
import com.hyperion.di.serviceModule
import com.hyperion.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HyperionApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HyperionApplication)
            modules(httpModule, serviceModule, repositoryModule, viewModelModule)
        }
    }
}