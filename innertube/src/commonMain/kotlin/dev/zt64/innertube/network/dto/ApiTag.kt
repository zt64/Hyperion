package dev.zt64.innertube.network.dto

import dev.zt64.innertube.network.dto.browse.ApiBrowse
import dev.zt64.innertube.network.dto.renderer.VideoRenderer
import dev.zt64.innertube.serializer.SingletonMapPolymorphicSerializer
import dev.zt64.innertube.serializer.TokenSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
internal data class ApiTagParams(
    @ProtoNumber(93)
    val context: Context
) {
    @Serializable
    data class Context(
        @ProtoNumber(1)
        val tag: String
    )
}

internal typealias ApiTagRenderer = @Serializable(with = ApiTag.Renderer.Serializer::class) ApiTag.Renderer

@Serializable
internal data class ApiTag(
    val header: Header,
    override val contents: Contents<ApiTagRenderer>
) : ApiBrowse() {
    @Serializable
    data class Header(val hashtagHeaderRenderer: HashtagHeaderRenderer) {
        @Serializable
        data class HashtagHeaderRenderer(
            val hashtag: SimpleText,
            val hashtagInfoText: SimpleText
        )
    }

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
    @SerialName("continuationItemRenderer")
    data class ContinuationItem(
        @Serializable(TokenSerializer::class)
        @SerialName("continuationEndpoint")
        val token: String
    ) : Renderer
}

internal typealias ApiTagContinuation = Continuation<ApiTagRenderer>