package com.zt.innertube.network.dto.browse

import com.zt.innertube.domain.model.DomainChannelPartial
import com.zt.innertube.network.dto.*
import com.zt.innertube.serializer.SingletonMapPolymorphicSerializer
import com.zt.innertube.serializer.TokenSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    ) : Renderer
}

@Serializable
internal data class VideoRenderer(
    val channelThumbnailSupportedRenderers: ChannelThumbnailSupportedRenderers? = null,
    val lengthText: SimpleText? = null,
    val navigationEndpoint: NavigationEndpoint,
    val ownerText: ApiText,
    val publishedTimeText: SimpleText? = null,
    val shortViewCountText: ViewCount? = null,
    val thumbnail: ApiImage,
    val title: ApiText,
    val videoId: String,
    val viewCountText: SimpleText? = null
) {
    @Serializable
    data class NavigationEndpoint(val watchEndpoint: ApiWatchEndpoint? = null)
}

internal typealias ApiRecommendedContinuation = ApiBrowseContinuation<@Serializable(ApiRecommended.Renderer.Serializer::class) ApiRecommended.Renderer>

@Serializable
internal data class ChannelThumbnailSupportedRenderers(val channelThumbnailWithLinkRenderer: ChannelThumbnailWithLinkRenderer) {
    fun toDomain() = DomainChannelPartial(
        id = channelThumbnailWithLinkRenderer.navigationEndpoint.browseEndpoint.browseId,
        avatarUrl = channelThumbnailWithLinkRenderer.thumbnail.sources.last().url
    )

    @Serializable
    data class ChannelThumbnailWithLinkRenderer(
        val navigationEndpoint: ApiNavigationEndpoint,
        val thumbnail: ApiImage
    )
}