package dev.zt64.hyperion.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.io.File

internal val platformModule = module {
    singleOf(::Platform)
}

internal expect class Platform() {
    fun getDownloadsDir(): File
}