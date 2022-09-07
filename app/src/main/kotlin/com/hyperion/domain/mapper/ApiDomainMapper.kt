package com.hyperion.domain.mapper

import com.hyperion.domain.model.DomainChannelPartial
import com.hyperion.domain.model.DomainStream
import com.hyperion.domain.model.DomainVideoPartial
import com.hyperion.network.dto.ApiFormat
import com.hyperion.network.dto.ApiNextVideo
import com.hyperion.network.dto.ApiVideo

fun ApiVideo.ContextData.toDomain() = DomainVideoPartial(
    id = onTap.innertubeCommand.watchEndpoint?.videoId.orEmpty(),
    title = videoData.metadata.title,
    subtitle = videoData.metadata.metadataDetails,
    timestamp = videoData.thumbnail.timestampText,
    channel = videoData.avatar?.let { avatar ->
        DomainChannelPartial(
            id = avatar.endpoint.innertubeCommand.browseEndpoint.browseId,
            avatarUrl = avatar.image.sources.last().url
        )
    } ?: DomainChannelPartial(
        id = videoData.channelId!!,
        avatarUrl = videoData.decoratedAvatar!!.avatar.image.sources.last().url
    )
)

fun ApiNextVideo.ContextData.toDomain() = DomainVideoPartial(
    id = onTap.innertubeCommand.watchNextWatchEndpointMutationCommand?.watchEndpoint?.watchEndpoint?.videoId.orEmpty(),
    title = videoData.metadata.title,
    subtitle = videoData.metadata.metadataDetails,
    timestamp = videoData.thumbnail.timestampText,
    channel = videoData.avatar?.let { avatar ->
        DomainChannelPartial(
            id = avatar.endpoint.innertubeCommand.browseEndpoint.browseId,
            avatarUrl = avatar.image.sources.last().url
        )
    }
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