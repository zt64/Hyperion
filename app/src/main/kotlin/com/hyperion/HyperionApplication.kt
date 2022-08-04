package com.hyperion

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.hyperion.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HyperionApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HyperionApplication)
            modules(httpModule, serviceModule, repositoryModule, viewModelModule, managerModule)
        }
    }

    override fun newImageLoader() = ImageLoader.Builder(this)
        .crossfade(true)
        .build()
}