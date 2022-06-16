package com.hyperion.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val httpModule = module {
    fun provideInnerTubeClient(
        json: Json
    ) = HttpClient(CIO) {
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

        install(HttpCache)
    }

    single {
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
    }

    single { provideInnerTubeClient(get()) }
}