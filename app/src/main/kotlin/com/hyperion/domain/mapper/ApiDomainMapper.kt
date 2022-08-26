package com.hyperion.domain.mapper

import com.hyperion.domain.model.DomainChannel
import com.hyperion.domain.model.DomainChannelPartial
import com.hyperion.domain.model.DomainStream
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.network.dto.ApiChannel
import com.hyperion.network.dto.ApiFormat
import com.hyperion.network.dto.ApiTrending
import com.hyperion.network.dto.ApiVideo

fun ApiChannel.toDomain(): DomainChannel {
    val channelHeaderModal = header.channelMobileHeaderRenderer.channelHeader.elementRenderer.model.channelHeaderModel

    val channelProfile = channelHeaderModal.channelProfile
    val avatars = channelProfile.avatarData.avatar.image.sources
    val banners = channelHeaderModal.channelBanner?.image?.sources

    val tabRenderer = contents.browseResultsRenderer.tabs.first().tabRenderer

    return DomainChannel(
        id = tabRenderer.endpoint.browseEndpoint.browseId,
        name = channelProfile.title,
        description = channelProfile.descriptionPreview.description,
        subscriberText = channelHeaderModal.channelProfile.metadata.subscriberCountText,
        avatar = avatars.first().url,
        banner = banners?.lastOrNull()?.url,
        videos = tabRenderer.content.sectionListRenderer.contents
            .mapNotNull { it.shelfRenderer }
            .mapNotNull { (shelfRenderer) ->
                shelfRenderer.horizontalListRenderer?.items
                    ?.firstOrNull()?.elementRenderer?.model?.gridVideoModel
                    ?.let { (videoData, onTap) ->
                        DomainVideoPartial(
                            id = onTap.innertubeCommand.watchEndpoint?.videoId ?: "",
                            title = videoData.metadata.title,
                            subtitle = videoData.metadata.metadataDetails,
                            timestamp = videoData.thumbnail.timestampText
                        )
                    }
            }
    )
}

fun ApiTrending.VideoWithContextModel.VideoWithContextData.toDomain() = DomainVideoPartial(
    id = onTap.innertubeCommand.watchEndpoint.videoId,
    title = videoData.metadata.title,
    subtitle = videoData.metadata.metadataDetails,
    timestamp = videoData.thumbnail.timestampText,
    author = DomainChannelPartial(
        id = videoData.avatar.endpoint.innertubeCommand.browseEndpoint.browseId,
        avatarUrl = videoData.avatar.image.sources.last().url
    )
)

fun ApiFormat.toDomain() = when {
    mimeType.startsWith("video/") -> DomainStream.Video(
        url = url,
        itag = itag,
        label = qualityLabel!!,
        mimeType = mimeType
    )
    mimeType.startsWith("audio/") -> DomainStream.Audio(
        url = url,
        itag = itag,
        mimeType = mimeType
    )
    else -> throw NoWhenBranchMatchedException(toString())
}

fun ApiVideo.ContextData.toDomain() = DomainVideoPartial(
    id = onTap.innertubeCommand.watchNextWatchEndpointMutationCommand?.watchEndpoint?.watchEndpoint?.videoId.orEmpty(),
    title = videoData.metadata.title,
    subtitle = videoData.metadata.metadataDetails,
    timestamp = videoData.thumbnail.timestampText,
    author = videoData.avatar?.let {
        DomainChannelPartial(
            id = videoData.avatar.endpoint.innertubeCommand.browseEndpoint.browseId,
            avatarUrl = videoData.avatar.image.sources.last().url
        )
    }
)