package dev.zt64.hyperion.api.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal typealias ApiText =
    @Serializable(ApiTextSerializer::class)
    String

internal object ApiTextSerializer : JsonTransformingSerializer<String>(String.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement = element
        .jsonObject["runs"]!!
        .jsonArray
        .joinToString(separator = "") {
            it.jsonObject["text"]!!.jsonPrimitive.content
        }.let(::JsonPrimitive)
}

@Serializable
internal data class ApiTextClickable(val runs: List<Run>) {
    @Serializable
    data class Run(val navigationEndpoint: ApiNavigationEndpoint, val text: String)
}

@Serializable
internal class ElementsAttributedText private constructor(
    @Serializable(Serializer::class)
    @SerialName("elementsAttributedString")
    val text: String
) {
    override fun toString() = text

    private object Serializer : JsonTransformingSerializer<String>(String.serializer()) {
        override fun transformDeserialize(element: JsonElement) = element.jsonObject["content"]!!
    }
}

internal typealias SimpleText =
    @Serializable(SimpleTextSerializer::class)
    String

internal object SimpleTextSerializer : JsonTransformingSerializer<String>(String.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        element.jsonObject["simpleText"]?.jsonPrimitive ?: JsonPrimitive("AAAAAAAAA")
}