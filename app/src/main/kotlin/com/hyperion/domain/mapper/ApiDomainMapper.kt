package com.hyperion.domain.mapper

import com.hyperion.domain.model.DomainChannel
import com.hyperion.domain.model.DomainChannelPartial
import com.hyperion.domain.model.DomainStream
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.network.dto.ApiChannel
import com.hyperion.network.dto.ApiChannelVideo
import com.hyperion.network.dto.ApiFormat
import com.hyperion.network.dto.ApiTrending

fun ApiChannel.toDomain(): DomainChannel {
    val channelProfile = channelHeaderModal.channelProfile
    val avatars = channelProfile.avatarData.avatar.image.sources
    val banners = channelHeaderModal.channelBanner.image.sources

    val tabRenderer = contents.singleColumnBrowseResultsRenderer.tabs[0].tabRenderer

    return DomainChannel(
        id = tabRenderer.endpoint.browseEndpoint.browseId,
        name = channelProfile.title,
        description = channelProfile.descriptionPreview.description,
        subscriberText = channelHeaderModal.channelProfile.metadata.subscriberCountText,
        avatar = avatars[0].url,
        banner = banners[0].url,
        videos = tabRenderer.content.sectionListRenderer.contents?.mapNotNull {
            it.shelfRenderer.content.horizontalListRenderer?.items?.get(0)?.elementRenderer?.newElement?.type?.componentType?.model?.gridVideoModel?.toDomain()
        } ?: emptyList()
    )
}

fun ApiChannelVideo.toDomain() = DomainVideoPartial(
    id = onTap.innertubeCommand.watchEndpoint?.videoId ?: "",
    title = videoData.metadata.title,
    subtitle = videoData.metadata.metadataDetails,
    thumbnailUrl = videoData.thumbnail.image.sources.last().url,
    timestamp = videoData.thumbnail.timestampText
)

fun ApiTrending.ApiVideoContext.toDomain() = DomainVideoPartial(
    id = videoId,
    title = headline.toString(),
    subtitle = buildString {
        append("${shortBylineText.runs[0].text} - ")
        shortViewCountText?.let { append("$it - ") }
        append(publishedTimeText.toString())
    },
    thumbnailUrl = thumbnail.thumbnails.last().url,
    timestamp = lengthText.toString(),
    author = DomainChannelPartial(
        id = shortBylineText.runs[0].navigationEndpoint.browseEndpoint.browseId,
        avatarUrl = channelThumbnail.channelThumbnailWithLinkRenderer.thumbnail.thumbnails[0].url
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