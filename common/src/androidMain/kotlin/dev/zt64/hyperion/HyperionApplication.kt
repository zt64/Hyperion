package dev.zt64.hyperion

import android.app.Application
import dev.zt64.hyperion.di.viewModelModule
import org.koin.android.ext.koin.androidContext

class HyperionApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initializeKoin {
            androidContext(this@HyperionApplication)
            modules(viewModelModule)
        }
    }
}