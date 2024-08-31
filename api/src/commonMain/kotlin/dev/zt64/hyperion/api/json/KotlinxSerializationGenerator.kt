package dev.zt64.hyperion.api.json

import com.google.api.client.json.JsonFactory
import com.google.api.client.json.JsonGenerator
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToStream
import java.io.OutputStream
import java.math.BigDecimal
import java.math.BigInteger

@OptIn(ExperimentalSerializationApi::class)
class KotlinxSerializationGenerator(private val outputStream: OutputStream, private val factory: KotlinxSerializationFactory) :
    JsonGenerator() {

    private var jsonObjectBuilder: MutableMap<String, JsonElement>? = null
    private var jsonArrayBuilder: MutableList<JsonElement>? = null
    private var currentFieldName: String? = null

    init {
        startNewJsonObject()
    }

    override fun flush() {
        outputStream.flush()
    }

    override fun close() {
        flush()
        outputStream.close()
    }

    override fun getFactory(): JsonFactory {
        return factory
    }

    override fun writeBoolean(state: Boolean) {
        addElement(JsonPrimitive(state))
    }

    override fun writeEndArray() {
        jsonArrayBuilder?.let { finishJsonArray() }
    }

    override fun writeEndObject() {
        jsonObjectBuilder?.let { finishJsonObject() }
    }

    override fun writeFieldName(name: String) {
        currentFieldName = name
    }

    override fun writeNull() {
        addElement(JsonNull)
    }

    override fun writeNumber(value: Int) {
        addElement(JsonPrimitive(value))
    }

    override fun writeNumber(value: Long) {
        addElement(JsonPrimitive(value))
    }

    override fun writeNumber(value: BigInteger) {
        addElement(JsonPrimitive(value.toString()))
    }

    override fun writeNumber(value: Double) {
        addElement(JsonPrimitive(value))
    }

    override fun writeNumber(value: Float) {
        addElement(JsonPrimitive(value))
    }

    override fun writeNumber(value: BigDecimal) {
        addElement(JsonPrimitive(value.toString()))
    }

    override fun writeNumber(encodedValue: String) {
        addElement(JsonPrimitive(encodedValue))
    }

    override fun writeStartArray() {
        jsonArrayBuilder = mutableListOf()
    }

    override fun writeStartObject() {
        startNewJsonObject()
    }

    override fun writeString(value: String) {
        addElement(JsonPrimitive(value))
    }

    override fun enablePrettyPrint() {
        // Pretty print is handled by the Json object itself in Kotlinx Serialization.
    }

    private fun addElement(element: JsonElement) {
        when {
            jsonArrayBuilder != null -> jsonArrayBuilder!!.add(element)
            jsonObjectBuilder != null && currentFieldName != null -> {
                jsonObjectBuilder!![currentFieldName!!] = element
                currentFieldName = null
            }
            else -> throw IllegalStateException("No active JSON structure")
        }
    }

    private fun startNewJsonObject() {
        jsonObjectBuilder = mutableMapOf()
    }

    private fun finishJsonObject() {
        val jsonObject = buildJsonObject { jsonObjectBuilder!!.forEach { (key, value) -> put(key, value) } }
        factory.json.encodeToStream(JsonElement.serializer(), jsonObject, outputStream)
        jsonObjectBuilder = null
    }

    private fun finishJsonArray() {
        val jsonArray = buildJsonArray { jsonArrayBuilder!!.forEach { add(it) } }
        factory.json.encodeToStream(JsonElement.serializer(), jsonArray, outputStream)
        jsonArrayBuilder = null
    }
}