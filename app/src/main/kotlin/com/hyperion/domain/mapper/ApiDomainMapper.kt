package com.hyperion.domain.mapper

import com.hyperion.domain.model.DomainChannel
import com.hyperion.domain.model.DomainChannelPartial
import com.hyperion.domain.model.DomainStream
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.network.dto.*

fun ApiChannel.toDomain(): DomainChannel {
    val channelHeaderModal = header.channelMobileHeaderRenderer.channelHeader.elementRenderer.model.channelHeaderModel

    val channelProfile = channelHeaderModal.channelProfile
    val avatars = channelProfile.avatarData.avatar.image.sources
    val banners = channelHeaderModal.channelBanner?.image?.sources

    val tabRenderer = contents.singleColumnBrowseResultsRenderer.tabs.first().tabRenderer

    return DomainChannel(
        id = tabRenderer.endpoint.browseEndpoint.browseId,
        name = channelProfile.title,
        description = channelProfile.descriptionPreview.description,
        subscriberText = channelHeaderModal.channelProfile.metadata.subscriberCountText,
        avatar = avatars.first().url,
        banner = banners?.lastOrNull()?.url,
        videos = tabRenderer.content.sectionListRenderer.contents?.mapNotNull {
            it.shelfRenderer?.content?.horizontalListRenderer?.items?.firstOrNull()?.elementRenderer?.newElement?.type?.componentType?.model?.gridVideoModel?.toDomain()
        } ?: emptyList()
    )
}

fun ApiChannelVideo.toDomain() = DomainVideoPartial(
    id = onTap.innertubeCommand.watchEndpoint?.videoId ?: "",
    title = videoData.metadata.title,
    subtitle = videoData.metadata.metadataDetails,
    timestamp = videoData.thumbnail.timestampText
)

fun ApiTrending.VideoWithContextData.toDomain() = DomainVideoPartial(
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
//    Commented out as I'm unsure how to get audio to play properly
//    mimeType.contains("video/mp4") -> DomainStream.Video(
//        url = url,
//        itag = itag,
//        label = qualityLabel!!
//    )
//    mimeType.startsWith("audio/mp4") -> DomainStream.Audio(
//        url = url,
//        itag = itag,
//        codec = mimeType
//    )
    else -> DomainStream(url)
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