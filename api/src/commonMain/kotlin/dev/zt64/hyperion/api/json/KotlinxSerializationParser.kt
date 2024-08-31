package dev.zt64.hyperion.api.json

import com.google.api.client.json.JsonFactory
import com.google.api.client.json.JsonParser
import com.google.api.client.json.JsonToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.util.ArrayDeque

@OptIn(ExperimentalSerializationApi::class)
class KotlinxSerializationParser(private val factory: KotlinxSerializationFactory, inputStream: InputStream) : JsonParser() {

    private val rootElement: JsonElement = factory.json.decodeFromStream(JsonElement.serializer(), inputStream)
    private val elementStack: ArrayDeque<Pair<String?, JsonElement>> = ArrayDeque()
    private var currentToken: JsonToken? = null
    private var currentElement: JsonElement? = rootElement
    private var currentFieldName: String? = null

    init {
        pushElement(rootElement)
        updateState()
    }

    private fun pushElement(element: JsonElement?, fieldName: String? = null) {
        elementStack.addLast(fieldName to element!!)
    }

    private fun processPrimitive(element: JsonPrimitive): JsonToken {
        return when {
            element.isString -> JsonToken.VALUE_STRING
            element.booleanOrNull != null -> if (element.booleanOrNull == true) JsonToken.VALUE_TRUE else JsonToken.VALUE_FALSE
            element.intOrNull != null || element.longOrNull != null || element.doubleOrNull != null -> JsonToken.VALUE_NUMBER_FLOAT
            else -> JsonToken.VALUE_NULL
        }
    }

    private fun updateState() {
        currentToken = when {
            currentElement is JsonObject -> JsonToken.START_OBJECT
            currentElement is JsonArray -> JsonToken.START_ARRAY
            currentElement is JsonPrimitive -> processPrimitive(currentElement as JsonPrimitive)
            else -> JsonToken.VALUE_NULL
        }

        if (currentToken == JsonToken.START_OBJECT || currentToken == JsonToken.START_ARRAY) {
            pushElement(currentElement)
        }
    }

    override fun nextToken(): JsonToken? {
        println("Current Token: $currentToken, Current Element: $currentElement, Element Stack Size: ${elementStack.size}")

        if (currentElement != null) pushElement(currentElement, currentFieldName)
        currentFieldName = null

        while (elementStack.isNotEmpty()) {
            val (key, value) = elementStack.removeLast()

            currentElement = value
            currentFieldName = key

            currentToken = when {
                key != null -> JsonToken.FIELD_NAME
                value is JsonObject -> {
                    pushElement(value)
                    JsonToken.START_OBJECT
                }
                value is JsonArray -> {
                    pushElement(value)
                    JsonToken.START_ARRAY
                }
                value is JsonPrimitive -> processPrimitive(value)
                else -> JsonToken.VALUE_NULL
            }

            return currentToken
        }

        currentToken = null
        return currentToken
    }

    override fun skipChildren(): JsonParser {
        var depth = 1
        while (depth > 0) {
            nextToken()
            when (currentToken) {
                JsonToken.START_OBJECT, JsonToken.START_ARRAY -> depth++
                JsonToken.END_OBJECT, JsonToken.END_ARRAY -> depth--
                else -> {} // No action required for other token types
            }
        }
        return this
    }

    override fun getCurrentName(): String? {
        return currentFieldName
    }

    override fun getCurrentToken(): JsonToken? {
        return currentToken
    }

    override fun getText(): String? {
        return currentFieldName ?: currentElement.toString()
    }

    override fun getFactory(): JsonFactory {
        return factory
    }

    override fun close() {
        // No-op for this implementation
    }

    @Throws(IOException::class)
    private fun checkNumber() {
        if (currentToken != JsonToken.VALUE_NUMBER_FLOAT && currentToken != JsonToken.VALUE_NUMBER_INT) {
            throw IOException("The current token is not a number")
        }
    }

    override fun getByteValue(): Byte {
        checkNumber()
        return currentElement?.jsonPrimitive?.content?.toByte() ?: 0
    }

    override fun getShortValue(): Short {
        checkNumber()
        return currentElement?.jsonPrimitive?.content?.toShort() ?: 0
    }

    override fun getIntValue(): Int {
        checkNumber()
        return currentElement?.jsonPrimitive?.content?.toInt() ?: 0
    }

    override fun getLongValue(): Long {
        checkNumber()
        return currentElement?.jsonPrimitive?.content?.toLong() ?: 0
    }

    override fun getFloatValue(): Float {
        checkNumber()
        return currentElement?.jsonPrimitive?.content?.toFloat() ?: 0f
    }

    override fun getDoubleValue(): Double {
        checkNumber()
        return currentElement?.jsonPrimitive?.content?.toDouble() ?: 0.0
    }

    override fun getBigIntegerValue(): BigInteger {
        checkNumber()
        return BigInteger(currentElement?.jsonPrimitive?.content ?: "0")
    }

    override fun getDecimalValue(): BigDecimal {
        checkNumber()
        return BigDecimal(currentElement?.jsonPrimitive?.content ?: "0.0")
    }
}