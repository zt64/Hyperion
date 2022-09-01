package com.hyperion.di

import android.os.Build
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val httpModule = module {
    fun provideJson() = Json {
        ignoreUnknownKeys = true
    }

    fun provideInnerTubeClient(json: Json) = HttpClient(
        engineFactory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CIO
        } else {
            Android
        }
    ) {
        BrowserUserAgent()

        install(ContentNegotiation) {
            json(json)
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
            exponentialDelay()
        }

        install(ContentEncoding) {
            deflate()
            gzip()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            install(HttpCache)
        }
    }

    singleOf(::provideJson)
    singleOf(::provideInnerTubeClient)
}