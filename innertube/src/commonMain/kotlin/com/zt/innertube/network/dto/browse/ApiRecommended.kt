package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.*
import com.zt.innertube.serializer.SingletonMapPolymorphicSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject

@Serializable
internal data class ApiRecommended(
    override val contents: Contents<@Serializable(Renderer.Serializer::class) Renderer>
) : ApiBrowse() {
    @Serializable
    sealed interface Renderer {
        object Serializer : SingletonMapPolymorphicSerializer<Renderer>(serializer())
    }

    @Serializable
    @SerialName("richItemRenderer")
    data class RichItemRenderer(val content: Content) : Renderer {
        @Serializable
        data class Content(val videoRenderer: VideoRenderer? = null)
    }

    @Serializable
    @SerialName("richSectionRenderer")
    object RichSectionRenderer : Renderer

    @Serializable
    @SerialName("continuationItemRenderer")
    data class ContinuationItem(
        @Serializable(TokenSerializer::class)
        @SerialName("continuationEndpoint")
        val token: String
    ) : Renderer {
        private class TokenSerializer : JsonTransformingSerializer<String>(String.serializer()) {
            override fun transformDeserialize(element: JsonElement): JsonElement {
                return element.jsonObject["continuationCommand"]!!.jsonObject["token"]!!
            }
        }
    }
}

@Serializable
internal data class VideoRenderer(
    val channelThumbnailSupportedRenderers: ChannelThumbnailSupportedRenderers? = null,
    val lengthText: SimpleText? = null,
    val navigationEndpoint: NavigationEndpoint,
    val ownerText: ApiText,
    val publishedTimeText: SimpleText? = null,
    @Serializable(ViewCountSerializer::class)
    val shortViewCountText: String? = null,
    val thumbnail: ApiImage,
    val title: ApiText,
    val videoId: String,
    val viewCountText: SimpleText? = null
) {
    @Serializable
    data class ChannelThumbnailSupportedRenderers(val channelThumbnailWithLinkRenderer: ChannelThumbnailWithLinkRenderer) {
        @Serializable
        data class ChannelThumbnailWithLinkRenderer(
            val navigationEndpoint: ApiNavigationEndpoint,
            val thumbnail: ApiImage
        )
    }

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
    data class NavigationEndpoint(val watchEndpoint: ApiWatchEndpoint? = null)
}

@Serializable
internal data class ApiRecommendedContinuation(
    override val onResponseReceivedActions: List<ContinuationContents<@Serializable(ApiRecommended.Renderer.Serializer::class) ApiRecommended.Renderer>>
) : ApiBrowseContinuation()