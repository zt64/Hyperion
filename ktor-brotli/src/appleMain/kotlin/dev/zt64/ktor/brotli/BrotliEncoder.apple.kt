package dev.zt64.ktor.brotli

import io.ktor.utils.io.ByteReadChannel

internal actual fun ByteReadChannel.decodeBrotli(): ByteReadChannel {
    return this // Probably not the best way to do this
}