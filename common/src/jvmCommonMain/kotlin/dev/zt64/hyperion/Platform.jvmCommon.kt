package dev.zt64.hyperion

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun httpClientEngineFactory(): HttpClientEngineFactory<HttpClientEngineConfig> {
    return OkHttp
}