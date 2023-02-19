package com.zt.innertube.domain.mapper

import com.zt.innertube.domain.model.DomainChannelPartial
import com.zt.innertube.domain.model.DomainVideoPartial
import com.zt.innertube.network.dto.ApiNextVideo
import com.zt.innertube.network.dto.ApiVideo
import com.zt.innertube.network.dto.ContextData

internal fun ApiVideo.toDomain() = videoWithContextData.toDomain(
    id = videoWithContextData.onTap.innertubeCommand.watchEndpoint?.videoId.orEmpty()
)

internal fun ApiNextVideo.toDomain() = videoWithContextData.toDomain(
    id = videoWithContextData.onTap.innertubeCommand.watchNextWatchEndpointMutationCommand
        ?.watchEndpoint?.watchEndpoint?.videoId.orEmpty()
)

private fun ContextData<*>.toDomain(id: String) = DomainVideoPartial(
    id = id,
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
        avatarUrl = videoData.decoratedAvatar!!.sources.last().url
    )
)