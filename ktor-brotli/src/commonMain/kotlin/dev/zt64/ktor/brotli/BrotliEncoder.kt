package dev.zt64.ktor.brotli

import io.ktor.client.plugins.compression.ContentEncodingConfig
import io.ktor.util.ContentEncoder
import io.ktor.util.Encoder
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlin.coroutines.CoroutineContext

object BrotliEncoder : ContentEncoder, Encoder by BrotliEncoder {
    override val name = "br"

    override fun decode(
        source: ByteReadChannel,
        coroutineContext: CoroutineContext
    ): ByteReadChannel = source.decodeBrotli()

    override fun encode(
        source: ByteReadChannel,
        coroutineContext: CoroutineContext
    ): ByteReadChannel {
        error("BrotliOutputStream not available (https://github.com/google/brotli/issues/715)")
    }

    override fun encode(
        source: ByteWriteChannel,
        coroutineContext: CoroutineContext
    ): ByteWriteChannel {
        error("BrotliOutputStream not available (https://github.com/google/brotli/issues/715)")
    }
}

fun ContentEncodingConfig.brotli(quality: Float? = null) {
    customEncoder(BrotliEncoder, quality)
}

internal expect fun ByteReadChannel.decodeBrotli(): ByteReadChannel