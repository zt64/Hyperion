package dev.zt64.hyperion.api.json

import com.google.api.client.json.JsonFactory
import com.google.api.client.json.JsonGenerator
import com.google.api.client.json.JsonParser
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class KotlinxSerializationFactory private constructor(val json: Json) : JsonFactory() {

    companion object {
        fun getDefaultInstance(): KotlinxSerializationFactory {
            return InstanceHolder.INSTANCE
        }

        private object InstanceHolder {
            val INSTANCE = KotlinxSerializationFactory(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
    }

    override fun createJsonParser(inputStream: InputStream): JsonParser {
        return KotlinxSerializationParser(this, inputStream)
    }

    override fun createJsonParser(inputStream: InputStream, charset: Charset): JsonParser {
        return KotlinxSerializationParser(this, inputStream)
    }

    override fun createJsonParser(value: String): JsonParser {
        return KotlinxSerializationParser(this, value.byteInputStream(StandardCharsets.UTF_8))
    }

    override fun createJsonParser(reader: Reader): JsonParser {
        return KotlinxSerializationParser(this, reader.readText().byteInputStream())
    }

    override fun createJsonGenerator(outputStream: OutputStream, charset: Charset): JsonGenerator {
        return KotlinxSerializationGenerator(outputStream, this)
    }

    override fun createJsonGenerator(writer: Writer): JsonGenerator {
        throw UnsupportedOperationException("Writer-based JSON generation is not supported. Use OutputStream instead.")
    }
}