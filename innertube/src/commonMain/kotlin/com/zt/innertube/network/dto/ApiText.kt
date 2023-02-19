package com.zt.innertube.network.dto

import kotlinx.serialization.Serializable

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
internal class ApiText private constructor(private val runs: List<TextRun>) {
    val text = runs.joinToString(separator = "", transform = TextRun::text)

    @Serializable
    data class TextRun(val text: String)

    override fun toString() = text
}

@Serializable
internal data class ElementsAttributedText(val elementsAttributedString: ElementsAttributedString) {
    override fun toString() = elementsAttributedString.content

    @Serializable
    data class ElementsAttributedString(val content: String)
}