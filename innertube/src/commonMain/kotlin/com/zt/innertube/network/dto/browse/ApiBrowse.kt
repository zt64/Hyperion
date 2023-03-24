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
        val browseResultsRenderer: BrowseResultsRenderer<SectionListRenderer<T>>
    ) {
        val content = browseResultsRenderer.content

        @Serializable
        data class BrowseResultsRenderer<T>(
            @Serializable(TabsSerializer::class)
            private val tabs: List<T>
        ) {
            val content = tabs.first()

            private class TabsSerializer<T : Any>(tSerializer: KSerializer<T>) : JsonTransformingSerializer<List<T>>(ListSerializer(tSerializer)) {
                override fun transformDeserialize(element: JsonElement) = JsonArray(
                    element
                        .jsonArray
                        .map {
                            it
                                .jsonObject["tabRenderer"]!!
                                .jsonObject["content"]!!
                                .jsonObject
                                .values
                                .last()
                        }
                )
            }
        }
    }
}

@Serializable
internal abstract class ApiBrowseContinuation {
    abstract val onResponseReceivedActions: List<ContinuationContents<*>>

    @Serializable
    data class ContinuationContents<T>(
        @Serializable(ContinuationItemsSerializer::class)
        @SerialName("appendContinuationItemsAction")
        val items: List<T>
    ) {
        private class ContinuationItemsSerializer<T>(tSerializer: KSerializer<T>) : JsonTransformingSerializer<List<T>>(ListSerializer(tSerializer)) {
            override fun transformDeserialize(element: JsonElement) = element.jsonObject["continuationItems"]!!
        }
    }
}