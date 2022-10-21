package com.hyperion.di

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val httpModule = module {
    fun provideJson() = Json {
        ignoreUnknownKeys = true
    }

    fun provideInnerTubeClient(json: Json) = HttpClient(
        engineFactory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) CIO else Android
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