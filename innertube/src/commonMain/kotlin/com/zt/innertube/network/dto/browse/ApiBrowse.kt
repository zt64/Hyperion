package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.renderer.SectionListRenderer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

@Serializable
internal abstract class ApiBrowse {
    abstract val contents: Contents<*>

    @Serializable
    data class Contents<T>(
        @SerialName("twoColumnBrowseResultsRenderer")
        @Serializable(TabsSerializer::class)
        val tabs: List<SectionListRenderer<T>>
    ) {
        val content = tabs.first()

        private class TabsSerializer<T : Any>(
            tSerializer: KSerializer<T>
        ) : JsonTransformingSerializer<List<T>>(ListSerializer(tSerializer)) {
            override fun transformDeserialize(element: JsonElement) = element
                .jsonObject["tabs"]!!.jsonArray
                .mapNotNull {
                    it
                        .jsonObject["tabRenderer"]
                        ?.jsonObject?.get("content")
                        ?.jsonObject
                        ?.values
                        ?.last()
                }
                .let(::JsonArray)
        }
    }
}

internal typealias ApiBrowseContinuation<T> = @Serializable(ContinuationSerializer::class) List<T>

internal class ContinuationSerializer<T : Any>(tSerializer: KSerializer<T>) : JsonTransformingSerializer<List<T>>(ListSerializer(tSerializer)) {
    override fun transformDeserialize(element: JsonElement) = element
        .jsonObject["onResponseReceivedActions"]!!
        .jsonArray.single()
        .jsonObject["appendContinuationItemsAction"]!!
        .jsonObject["continuationItems"]!!
}