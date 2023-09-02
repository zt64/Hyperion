package dev.zt64.innertube.network.dto.renderer

import dev.zt64.innertube.domain.model.*
import dev.zt64.innertube.network.dto.*
import dev.zt64.innertube.network.dto.browse.ChannelThumbnailSupportedRenderers
import dev.zt64.innertube.serializer.SingletonMapPolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal typealias Renderer = @Serializable(IRenderer.Serializer::class) IRenderer

@Serializable
internal sealed interface IRenderer {
    object Serializer : SingletonMapPolymorphicSerializer<IRenderer>(serializer())

    fun toDomain(): Entity? = null
}

@Serializable
@SerialName("channelRenderer")
internal data class ChannelRenderer(
    val channelId: String,
    val title: SimpleText,
    val thumbnail: ApiImage,
    @SerialName("videoCountText") // YouTube moment
    val subscriberCountText: SimpleText? = null
) : IRenderer {
    override fun toDomain() = DomainChannelPartial(
        id = channelId,
        name = title,
        avatarUrl = "https:${thumbnail.sources.last().url}",
        subscriptionsText = subscriberCountText
    )
}

@Serializable
@SerialName("videoRenderer")
internal data class VideoRenderer(
    val channelThumbnailSupportedRenderers: ChannelThumbnailSupportedRenderers? = null,
    val videoId: String,
    val thumbnail: ApiImage,
    val title: ApiText,
    val publishedTimeText: SimpleText? = null,
    val longBylineText: ApiText,
    val shortViewCountText: ViewCount? = null,
    val lengthText: SimpleText? = null,
    val ownerText: ApiText
) : IRenderer {
    override fun toDomain() = DomainVideoPartial(
        id = videoId,
        title = title,
        viewCount = shortViewCountText,
        publishedTimeText = publishedTimeText,
        ownerText = ownerText,
        timestamp = lengthText,
        channel = channelThumbnailSupportedRenderers?.toDomain()
    )
}

@Serializable
@SerialName("hashtagTileRenderer")
internal data class HashtagTileRenderer(
    val hashtag: SimpleText,
    val hashtagInfoText: SimpleText,
    val hashtagBackgroundColor: Long,
    val hashtagVideoCount: SimpleText,
    val hashtagChannelCount: SimpleText
) : IRenderer {
    override fun toDomain() = DomainTagPartial(
        name = hashtag,
        channelsCount = hashtagChannelCount,
        videosCount = hashtagVideoCount,
        backgroundColor = hashtagBackgroundColor,
    )
}

@Serializable
@SerialName("playlistRenderer")
internal data class PlaylistRenderer(
    val playlistId: String,
    val thumbnails: List<ApiImage>,
    val title: SimpleText,
    val videoCount: String,
    val shortBylineText: ApiText
) : IRenderer {
    override fun toDomain() = DomainPlaylistPartial(
        id = playlistId,
        title = title,
        subtitle = shortBylineText,
        videoCountText = videoCount,
        thumbnailUrl = thumbnails.first().sources.last().url
    )
}

@Serializable
internal data object UnknownRenderer : IRenderer