package com.hyperion.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val httpModule = module {
    fun provideJson() = Json {
        ignoreUnknownKeys = true
    }

    fun provideEngineFactory(): HttpClientEngineFactory<*> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) CIO else Android
    }

    fun provideHttpClient(engineFactory: HttpClientEngineFactory<*>, json: Json): HttpClient {
        return HttpClient(engineFactory) {
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
                brotli()
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                install(HttpCache)
            }
        }
    }

    singleOf(::provideEngineFactory)
    singleOf(::provideJson)
    singleOf(::provideHttpClient)
}