package dev.zt64.innertube.network.dto

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

internal typealias ViewCount = @Serializable(ViewCountSerializer::class) String

internal object ViewCountSerializer : JsonContentPolymorphicSerializer<String>(String::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<String> {
        return if (element.jsonObject.contains("runs")) {
            ApiTextSerializer
        } else {
            SimpleTextSerializer
        }
    }
}