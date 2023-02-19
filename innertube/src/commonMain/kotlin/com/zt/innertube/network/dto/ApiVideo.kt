package com.zt.innertube.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class VideoData(
    val avatar: ApiAvatar? = null,
    val decoratedAvatar: DecoratedAvatar? = null,
    val metadata: Metadata,
    val thumbnail: ApiThumbnailTimestamp,
    val channelId: String? = null
) {
    @Serializable
    data class Metadata(
        val title: String,
        val metadataDetails: String = "YouTube"
    )
}

@Serializable
internal data class ApiVideo(val videoWithContextData: ContextData<ApiWatchCommand>)

@Serializable
internal data class ApiNextVideo(val videoWithContextData: ContextData<InnertubeCommand>) {
    @Serializable
    data class InnertubeCommand(val watchNextWatchEndpointMutationCommand: MutationCommand? = null) {
        @Serializable
        data class MutationCommand(val watchEndpoint: ApiWatchCommand)
    }
}

@Serializable
internal data class ContextData<T>(
    val videoData: VideoData,
    val onTap: OnTap<T>
)