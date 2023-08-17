package dev.zt64.hyperion

import android.app.Application
import dev.zt64.hyperion.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HyperionApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HyperionApplication)
            modules(
                appModule,
                repositoryModule,
                viewModelModule
            )
            modules(commonModules())
        }
    }
}