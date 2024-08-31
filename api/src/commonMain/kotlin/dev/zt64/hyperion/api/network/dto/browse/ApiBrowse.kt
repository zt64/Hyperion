package dev.zt64.hyperion.api.network.dto.browse

import dev.zt64.hyperion.api.network.dto.renderer.SectionListRenderer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

@Serializable
internal class Browse<T>(override val contents: Contents<T>) : ApiBrowse()

@Serializable
internal abstract class ApiBrowse {
    abstract val contents: Contents<*>

    @Serializable
    data class Contents<T>(
        @SerialName("twoColumnBrowseResultsRenderer")
        @Serializable(TabsSerializer::class)
        val tabs: List<SectionListRenderer<T>>
    ) {
        val content: List<T>
            get() = tabs.first()

        private class TabsSerializer<T : Any>(tSerializer: KSerializer<T>) :
            JsonTransformingSerializer<List<T>>(ListSerializer(tSerializer)) {
            override fun transformDeserialize(element: JsonElement) = element
                .jsonObject["tabs"]!!
                .jsonArray
                .mapNotNull {
                    it
                        .jsonObject["tabRenderer"]
                        ?.jsonObject
                        ?.get("content")
                        ?.jsonObject
                        ?.values
                        ?.last()
                }.let(::JsonArray)
        }
    }
}