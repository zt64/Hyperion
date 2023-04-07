package com.zt.innertube.network.dto

import com.zt.innertube.network.dto.browse.ApiBrowse
import com.zt.innertube.network.dto.browse.ApiBrowseContinuation
import com.zt.innertube.network.dto.browse.VideoRenderer
import com.zt.innertube.network.dto.renderer.*
import com.zt.innertube.serializer.SingletonMapPolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
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

@Serializable
internal data class ApiTag(
    val header: Header,
    override val contents: Contents<@Serializable(with = Renderer.Serializer::class) Renderer>
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
    ) : Renderer {
        private class TokenSerializer : JsonTransformingSerializer<String>(String.serializer()) {
            override fun transformDeserialize(element: JsonElement): JsonElement {
                return element.jsonObject["continuationCommand"]!!.jsonObject["token"]!!
            }
        }
    }
}

@Serializable
internal data class ApiTagContinuation(
    override val onResponseReceivedActions: List<ContinuationContents<@Serializable(with = ApiTag.Renderer.Serializer::class) ApiTag.Renderer>>
) : ApiBrowseContinuation()