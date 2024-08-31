package dev.zt64.hyperion

import android.app.Application
import dev.zt64.hyperion.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HyperionApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Firebase.initialize(applicationContext, buildFirebaseOptions())
        //
        // runBlocking {
        //     val token = Firebase.installations.getId()
        // }

        startKoin {
            androidContext(this@HyperionApp)
            modules(appModule)
        }
    }
}