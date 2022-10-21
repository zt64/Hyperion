package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
class VideoData(
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
data class ApiVideo(val videoWithContextData: ContextData) {
    @Serializable
    data class ContextData(
        val videoData: VideoData,
        val onTap: OnTap<ApiWatchCommand>
    )
}

@Serializable
data class ApiNextVideo(val videoWithContextData: ContextData) {
    @Serializable
    data class ContextData(
        val videoData: VideoData,
        val onTap: OnTap<InnertubeCommand>
    ) {
        @Serializable
        data class InnertubeCommand(val watchNextWatchEndpointMutationCommand: MutationCommand? = null) {
            @Serializable
            data class MutationCommand(val watchEndpoint: ApiWatchCommand)
        }
    }
}