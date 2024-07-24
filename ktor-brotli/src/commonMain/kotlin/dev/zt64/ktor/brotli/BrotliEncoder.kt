package dev.zt64.ktor.brotli

import io.ktor.client.plugins.compression.ContentEncoder
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineScope

internal object BrotliEncoder : ContentEncoder {
    override val name = "br"

    override fun CoroutineScope.encode(source: ByteReadChannel) = encodeBrotli(source)

    override fun CoroutineScope.decode(source: ByteReadChannel) = decodeBrotli(source)
}

fun ContentEncoding.Config.brotli(quality: Float? = null) {
    customEncoder(BrotliEncoder, quality)
}

internal expect fun CoroutineScope.encodeBrotli(source: ByteReadChannel): ByteReadChannel

internal expect fun CoroutineScope.decodeBrotli(source: ByteReadChannel): ByteReadChannel