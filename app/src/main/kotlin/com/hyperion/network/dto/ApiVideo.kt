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
                val endpoint: Endpoint,
                val image: ApiImage
            ) {
                @Serializable
                data class Endpoint(val innertubeCommand: InnertubeCommand) {
                    @Serializable
                    data class InnertubeCommand(val browseEndpoint: ApiBrowseEndpoint)
                }
            }

            @Serializable
            data class Metadata(
                val maxTitleLine: Int,
                val metadataDetails: String = "YouTube",
                val title: String
            )
        }

        @Serializable
        data class OnTap(val innertubeCommand: InnertubeCommand) {
            @Serializable
            data class InnertubeCommand(val watchNextWatchEndpointMutationCommand: MutationCommand? = null) {
                @Serializable
                data class MutationCommand(val watchEndpoint: ApiWatchEndpoint)
            }
        }
    }
}