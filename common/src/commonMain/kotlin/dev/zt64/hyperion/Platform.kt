package dev.zt64.hyperion

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import java.io.File

internal expect val SUPPORTS_DYNAMIC_COLOR: Boolean
internal expect val SUPPORTS_GESTURES: Boolean
internal expect val SUPPORTS_PIP: Boolean

internal expect fun httpClientEngineFactory(): HttpClientEngineFactory<HttpClientEngineConfig>

internal expect object Platform {
    fun getDownloadsDir(): File
}