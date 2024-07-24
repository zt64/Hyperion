package dev.zt64.ktor.brotli

import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineScope

internal actual fun CoroutineScope.encodeBrotli(source: ByteReadChannel): ByteReadChannel {
}

internal actual fun CoroutineScope.decodeBrotli(source: ByteReadChannel): ByteReadChannel {
    TODO("Not yet implemented")
}