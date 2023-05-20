package com.zt.innertube.network.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject

internal typealias ViewCount = @Serializable(ViewCountSerializer::class) String

internal object ViewCountSerializer : KSerializer<String> {
    override val descriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): String {
        decoder as JsonDecoder

        val json = decoder.decodeJsonElement().jsonObject
        val serializer = if (json.contains("runs")) ApiTextSerializer else SimpleTextSerializer

        return decoder.json.decodeFromJsonElement(serializer, json)
    }

    override fun serialize(encoder: Encoder, value: String) = error("Not implemented")
}