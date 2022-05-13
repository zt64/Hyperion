package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiVideo(val videoWithContextData: VideoWithContextData) {
    @Serializable
    data class VideoWithContextData(val videoData: VideoData) {
        @Serializable
        data class VideoData(
            val avatar: Avatar,
            val metadata: Metadata,
            val thumbnail: Thumbnail
        ) {
            @Serializable
            data class Avatar(val image: ApiImage)

            @Serializable
            data class Metadata(
                val maxTitleLine: Int,
                val metadataDetails: String,
                val title: String
            )

            @Serializable
            data class Thumbnail(
                val image: ApiImage,
                val isVideoWithContext: Boolean,
                val timestampText: String
            )
        }
    }
}