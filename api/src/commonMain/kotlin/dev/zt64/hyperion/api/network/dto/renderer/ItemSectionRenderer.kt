package dev.zt64.hyperion.api.network.dto.renderer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject

internal typealias ItemSectionRenderer<T> =
    @Serializable(ItemSectionRendererSerializer::class)
    List<T>

internal class ItemSectionRendererSerializer<T : Any>(tSerializer: KSerializer<T>) :
    JsonTransformingSerializer<List<T>>(
        ListSerializer(tSerializer)
    ) {
    override fun transformDeserialize(element: JsonElement) = element.jsonObject["contents"]!!
}