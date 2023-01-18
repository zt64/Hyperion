package com.zt.ktor.brotli

import io.ktor.client.plugins.compression.ContentEncoder
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineScope
import org.brotli.dec.BrotliInputStream

internal object BrotliEncoder : ContentEncoder {
    override val name = "br"

    override fun CoroutineScope.encode(source: ByteReadChannel): ByteReadChannel {
        error("BrotliOutputStream not available (https://github.com/google/brotli/issues/715)")
    }

    override fun CoroutineScope.decode(source: ByteReadChannel): ByteReadChannel {
        return BrotliInputStream(source.toInputStream()).toByteReadChannel()
    }
}

fun ContentEncoding.Config.brotli(quality: Float? = null) {
    customEncoder(BrotliEncoder, quality)
}