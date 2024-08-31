package dev.zt64.hyperion.api.network.dto.browse

import dev.zt64.hyperion.api.domain.model.DomainChannelPartial
import dev.zt64.hyperion.api.network.dto.ApiImage
import dev.zt64.hyperion.api.network.dto.ApiNavigationEndpoint
import dev.zt64.hyperion.api.network.dto.Continuation
import dev.zt64.hyperion.api.network.dto.ContinuationSerializer
import dev.zt64.hyperion.api.network.dto.renderer.VideoRenderer
import dev.zt64.hyperion.api.serializer.SingletonMapPolymorphicSerializer
import dev.zt64.hyperion.api.serializer.TokenSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal typealias Renderer = @Serializable(IRenderer.Serializer::class) IRenderer

@Serializable
internal sealed interface IRenderer {
    object Serializer : SingletonMapPolymorphicSerializer<IRenderer>(serializer())
}

@Serializable
@SerialName("richItemRenderer")
internal data class RichItemRenderer(val content: Content) : IRenderer {
    @Serializable
    data class Content(val videoRenderer: VideoRenderer? = null)
}

@Serializable
@SerialName("richSectionRenderer")
internal data object RichSectionRenderer : IRenderer

@Serializable
@SerialName("continuationItemRenderer")
internal data class ContinuationItem(
    @Serializable(TokenSerializer::class)
    @SerialName("continuationEndpoint")
    val token: String
) : IRenderer

@Serializable
internal data object UnknownRenderer : IRenderer

@Serializable
internal data class ApiRecommended(override val contents: Contents<Renderer>) : ApiBrowse()

// internal typealias ApiRecommended = Browse<Renderer>
internal typealias ApiRecommendedContinuation = @Serializable(ContinuationSerializer::class) Continuation<Renderer>

@Serializable
internal data class ChannelThumbnailSupportedRenderers(val channelThumbnailWithLinkRenderer: ChannelThumbnailWithLinkRenderer) {
    fun toDomain() = DomainChannelPartial(
        id = channelThumbnailWithLinkRenderer.navigationEndpoint.browseEndpoint.browseId,
        avatarUrl = channelThumbnailWithLinkRenderer
            .thumbnail
            .sources
            .last()
            .url
    )

    @Serializable
    data class ChannelThumbnailWithLinkRenderer(val navigationEndpoint: ApiNavigationEndpoint, val thumbnail: ApiImage)
}