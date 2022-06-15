package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiVideo(val videoWithContextData: ContextData) {
    @Serializable
    data class ContextData(
        val videoData: VideoData,
        val onTap: OnTap
    ) {
        @Serializable
        data class VideoData(
            val avatar: Avatar? = null,
            val metadata: Metadata,
            val thumbnail: Thumbnail
        ) {
            @Serializable
            data class Avatar(
                val avatarImageSize: String,
                val endpoint: Endpoint,
                val image: ApiImage
            ) {
                @Serializable
                data class Endpoint(val innertubeCommand: InnertubeCommand) {
                    @Serializable
                    data class InnertubeCommand(val browseEndpoint: BrowseEndpoint) {
                        @Serializable
                        data class BrowseEndpoint(val browseId: String)
                    }
                }
            }

            @Serializable
            data class Metadata(
                val maxTitleLine: Int,
                val metadataDetails: String = "YouTube",
                val title: String
            )

            @Serializable
            data class Thumbnail(
                val image: ApiImage,
                val timestampText: String? = null,
            )
        }

        @Serializable
        data class OnTap(val innertubeCommand: InnertubeCommand) {
            @Serializable
            data class InnertubeCommand(val watchNextWatchEndpointMutationCommand: MutationCommand) {
                @Serializable
                data class MutationCommand(val watchEndpoint: WatchEndpoint) {
                    @Serializable
                    data class WatchEndpoint(val watchEndpoint: WatchEndpoint) {
                        @Serializable
                        data class WatchEndpoint(val videoId: String)
                    }
                }
            }
        }
    }
}