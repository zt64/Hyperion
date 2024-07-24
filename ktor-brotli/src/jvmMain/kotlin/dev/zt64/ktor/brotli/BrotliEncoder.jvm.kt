package dev.zt64.ktor.brotli

import io.ktor.util.cio.toByteReadChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineScope
import org.brotli.dec.BrotliInputStream

internal actual fun CoroutineScope.encodeBrotli(source: ByteReadChannel): ByteReadChannel {
    error("BrotliOutputStream not available (https://github.com/google/brotli/issues/715)")
}

internal actual fun CoroutineScope.decodeBrotli(source: ByteReadChannel): ByteReadChannel {
    return BrotliInputStream(source.toInputStream()).toByteReadChannel()
}