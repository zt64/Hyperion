package dev.zt64.hyperion.di

import dev.zt64.hyperion.httpClientEngineFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
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

internal val httpModule = module {
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    fun provideClientEngine(): HttpClientEngineFactory<*> = httpClientEngineFactory()

    fun provideHttpClient(
        engineFactory: HttpClientEngineFactory<*>,
        json: Json
    ): HttpClient {
        return HttpClient(engineFactory) {
            BrowserUserAgent()

            install(ContentNegotiation) {
                json(json)
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }

    singleOf(::provideClientEngine)
    singleOf(::provideJson)
    singleOf(::provideHttpClient)
}