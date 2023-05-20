package com.zt.innertube.network.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

internal typealias ApiText = @Serializable(ApiTextSerializer::class) String

internal object ApiTextSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ApiText", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        decoder as JsonDecoder

        return decoder.decodeJsonElement().jsonObject["runs"]!!.jsonArray.joinToString(separator = "") {
            it.jsonObject["text"]!!.jsonPrimitive.content
        }
    }

    override fun serialize(encoder: Encoder, value: String) {
        throw RuntimeException("Serialization is not needed")
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

    private object Serializer : JsonTransformingSerializer<String>(String.serializer()) {
        override fun transformDeserialize(element: JsonElement) = element.jsonObject["content"]!!
    }
}

internal typealias SimpleText = @Serializable(SimpleTextSerializer::class) String

internal object SimpleTextSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SimpleText", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        decoder as JsonDecoder

        return decoder.decodeJsonElement().jsonObject["simpleText"]?.jsonPrimitive?.content ?: "AAAAAAAAA"
    }

    override fun serialize(encoder: Encoder, value: String) {
        throw RuntimeException("Serialization is not needed")
    }
}