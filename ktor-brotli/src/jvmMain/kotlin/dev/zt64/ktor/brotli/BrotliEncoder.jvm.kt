package dev.zt64.ktor.brotli

import io.ktor.util.cio.toByteReadChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import org.brotli.dec.BrotliInputStream

internal actual fun ByteReadChannel.decodeBrotli(): ByteReadChannel {
    return BrotliInputStream(this.toInputStream()).toByteReadChannel()
}