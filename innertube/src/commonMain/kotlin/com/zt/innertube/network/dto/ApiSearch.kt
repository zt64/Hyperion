package com.zt.innertube.network.dto

import com.zt.innertube.network.dto.browse.ChannelThumbnailSupportedRenderers
import com.zt.innertube.network.dto.renderer.ItemSectionRendererSerializer
import com.zt.innertube.serializer.SingletonMapPolymorphicSerializer
import com.zt.innertube.serializer.TokenSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.protobuf.ProtoNumber

internal val searchModule = SerializersModule {
    polymorphicDefaultDeserializer(ApiSearch.Renderer::class) {
        ApiSearch.UnknownRenderer.serializer()
    }
}

@Serializable
internal class ApiSearchParams(
    @ProtoNumber(1) val sortBy: Int? = null,
    @ProtoNumber(2) val filters: Filters? = null,
    @ProtoNumber(19) val noFilter: Int? = null
) {
    @Serializable
    data class Filters(
        @ProtoNumber(1) val uploadDate: Int? = null,
        @ProtoNumber(2) val type: Int? = null,
        @ProtoNumber(3) val duration: Int? = null,
        @ProtoNumber(4) val featuresHd: Int? = null,
        @ProtoNumber(5) val featuresSubtitles: Int? = null,
        @ProtoNumber(6) val featuresCreativeCommons: Int? = null,
        @ProtoNumber(7) val features3d: Int? = null,
        @ProtoNumber(8) val featuresLive: Int? = null,
        @ProtoNumber(9) val featuresPurchased: Int? = null,
        @ProtoNumber(14) val features4k: Int? = null,
        @ProtoNumber(15) val features360: Int? = null,
        @ProtoNumber(23) val featuresLocation: Int? = null,
        @ProtoNumber(25) val featuresHdr: Int? = null,
        @ProtoNumber(26) val featuresVr180: Int? = null
    )
}

@Serializable
internal data class ApiSearch(
    @Serializable(ContentsSerializer::class)
    val contents: List<Item>
) {
    private object ContentsSerializer : JsonTransformingSerializer<List<Item>>(ItemSectionRendererSerializer(Item.Serializer)) {
        override fun transformDeserialize(element: JsonElement) = element
            .jsonObject["twoColumnSearchResultsRenderer"]!!
            .jsonObject["primaryContents"]!!
            .jsonObject["sectionListRenderer"]!!
    }

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
    ) : Item

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
        val shortViewCountText: ViewCount,
        val lengthText: SimpleText? = null,
        val ownerText: ApiText
    ) : Renderer

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
        private object ContinuationItemsSerializer : JsonTransformingSerializer<List<ApiSearch.Item>>(ListSerializer(ApiSearch.Item.Serializer)) {
            override fun transformDeserialize(element: JsonElement) = element.jsonObject["continuationItems"]!!
        }
    }
}