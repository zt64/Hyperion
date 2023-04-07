package com.zt.innertube.network.dto

import com.zt.innertube.network.dto.renderer.SectionListRenderer
import com.zt.innertube.serializer.SingletonMapPolymorphicSerializer
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule

internal val searchModule = SerializersModule {
    polymorphicDefaultDeserializer(ApiSearch.Renderer::class ) {
        ApiSearch.UnknownRenderer.serializer()
    }
}

@Serializable
internal class ApiSearchParams

@Serializable
internal data class ApiSearch(val contents: Contents) {
    @Serializable
    data class Contents(val twoColumnSearchResultsRenderer: TwoColumnSearchResultsRenderer)

    @Serializable
    data class TwoColumnSearchResultsRenderer(val primaryContents: PrimaryContents)

    @Serializable
    data class PrimaryContents(val sectionListRenderer: SectionListRenderer<@Serializable(Item.Serializer::class) Item>)

    @Serializable
    sealed interface Item {
        object Serializer : SingletonMapPolymorphicSerializer<Item>(serializer())
    }

    @Serializable
    @SerialName("itemSectionRenderer")
    data class ItemSection(val contents: List<@Serializable(Renderer.Serializer::class) Renderer>) : Item

    @Serializable
    @SerialName("continuationItemRenderer")
    data class ContinuationItem(
        @Serializable(TokenSerializer::class)
        @SerialName("continuationEndpoint")
        val token: String
    ) : Item {
        private class TokenSerializer : JsonTransformingSerializer<String>(String.serializer()) {
            override fun transformDeserialize(element: JsonElement): JsonElement {
                return element.jsonObject["continuationCommand"]!!.jsonObject["token"]!!
            }
        }
    }

    @Serializable
    sealed interface Renderer {
        object Serializer : SingletonMapPolymorphicSerializer<Renderer>(serializer())
    }

    @Serializable
    @SerialName("channelRenderer")
    data class ChannelRenderer(
        val channelId: String,
        val title: SimpleText,
        val thumbnail: ApiImage,
        @SerialName("videoCountText") // YouTube moment
        val subscriberCountText: SimpleText? = null
    ) : Renderer

    @Serializable
    @SerialName("videoRenderer")
    data class VideoRenderer(
        val channelThumbnailSupportedRenderers: ChannelThumbnailSupportedRenderers? = null,
        val videoId: String,
        val thumbnail: ApiImage,
        val title: ApiText,
        val publishedTimeText: SimpleText? = null,
        val longBylineText: ApiText,
        @Serializable(ViewCountSerializer::class)
        val shortViewCountText: String,
        val lengthText: SimpleText? = null,
        val ownerText: ApiText
    ) : Renderer {
        private class ViewCountSerializer : KSerializer<String> {
            override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Hell")

            override fun deserialize(decoder: Decoder): String {
                decoder as JsonDecoder

                val json = decoder.decodeJsonElement().jsonObject

                return if (json.contains("runs")) {
                    decoder.json.decodeFromJsonElement(ApiText.serializer(), json).text
                } else {
                    decoder.json.decodeFromJsonElement(SimpleText.serializer(), json).simpleText
                }
            }

            override fun serialize(encoder: Encoder, value: String) = TODO()
        }

        @Serializable
        data class ChannelThumbnailSupportedRenderers(val channelThumbnailWithLinkRenderer: ChannelThumbnailWithLinkRenderer) {
            @Serializable
            data class ChannelThumbnailWithLinkRenderer(
                val navigationEndpoint: ApiNavigationEndpoint,
                val thumbnail: ApiImage
            )
        }
    }

    @Serializable
    @SerialName("hashtagTileRenderer")
    data class HashtagTileRenderer(
        val hashtag: SimpleText,
        val hashtagInfoText: SimpleText,
        val hashtagBackgroundColor: Long,
        val hashtagVideoCount: SimpleText,
        val hashtagChannelCount: SimpleText
    ) : Renderer

    @Serializable
    @SerialName("playlistRenderer")
    data class PlaylistRenderer(
        val playlistId: String,
        val thumbnails: List<ApiImage>,
        val title: SimpleText,
        val videoCount: String,
        val shortBylineText: ApiText
    ) : Renderer

    @Serializable
    object UnknownRenderer : Renderer
}

@Serializable
internal data class ApiSearchContinuation(val onResponseReceivedCommands: List<Command>) {
    @Serializable
    data class Command(
        @Serializable(ContinuationItemsSerializer::class)
        @SerialName("appendContinuationItemsAction")
        val items: List<@Serializable(ApiSearch.Item.Serializer::class) ApiSearch.Item>
    ) {
        private class ContinuationItemsSerializer : JsonTransformingSerializer<List<ApiSearch.Item>>(ListSerializer(ApiSearch.Item.Serializer)) {
            override fun transformDeserialize(element: JsonElement) = element.jsonObject["continuationItems"]!!
        }
    }
}