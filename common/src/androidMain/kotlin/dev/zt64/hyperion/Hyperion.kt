package dev.zt64.hyperion

import android.app.Application
import dev.zt64.hyperion.di.commonModules
import dev.zt64.hyperion.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Hyperion : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Hyperion)
            commonModules()
            modules(viewModelModule)
        }
    }
}