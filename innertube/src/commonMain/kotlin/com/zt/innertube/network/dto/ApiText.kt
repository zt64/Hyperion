package com.zt.innertube.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*

// TODO: Switch to typealias for serialization to simplify API usage
//internal typealias ApiText = @Serializable(ApiTextSerializer::class) String
//
//internal object ApiTextSerializer : KSerializer<String> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ApiText", PrimitiveKind.STRING)
//
//    override fun deserialize(decoder: Decoder): String {
//        val input = decoder as JsonDecoder
//
//        return input.decodeJsonElement().jsonObject["runs"]!!.jsonArray.joinToString(separator = "") {
//            it.jsonObject["text"]!!.jsonPrimitive.content
//        }
//    }
//
//    override fun serialize(encoder: Encoder, value: String) {
//        throw RuntimeException("Serialization is not needed")
//    }
//}

@Serializable
internal open class ApiText(
    @Serializable(Serializer::class)
    @SerialName("runs")
    val text: String
) {
    override fun toString() = text

    private class Serializer : JsonTransformingSerializer<String>(String.serializer()) {
        override fun transformDeserialize(element: JsonElement) = JsonPrimitive(
            element.jsonArray.joinToString(separator = "") {
                it.jsonObject["text"]!!.jsonPrimitive.content
            }
        )
    }
}

@Serializable
internal data class ApiTextClickable(val runs: List<Run>) {
    @Serializable
    data class Run(
        val navigationEndpoint: ApiNavigationEndpoint,
        val text: String
    )
}

@Serializable
internal class ElementsAttributedText private constructor(
    @Serializable(Serializer::class)
    @SerialName("elementsAttributedString")
    val text: String
) {
    override fun toString() = text

    private class Serializer : JsonTransformingSerializer<String>(String.serializer()) {
        override fun transformDeserialize(element: JsonElement) = element.jsonObject["content"]!!
    }
}

@Serializable
internal class SimpleText(val simpleText: String = "AAAA") {
    override fun toString() = simpleText
}