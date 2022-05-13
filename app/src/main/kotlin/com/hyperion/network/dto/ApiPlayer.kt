package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiPlayer(
    val playbackTracking: PlaybackTracking,
    val streamingData: StreamingData,
    val videoDetails: VideoDetails
) {
    @Serializable
    data class PlaybackTracking(
        val videostatsDelayplayUrl: StatsUrl,
        val videostatsPlaybackUrl: StatsUrl,
        val videostatsWatchtimeUrl: StatsUrl,
    ) {
        @Serializable
        data class StatsUrl(val baseUrl: String)
    }

    @Serializable
    data class StreamingData(
        val formats: List<ApiFormat>,
        val adaptiveFormats: List<ApiFormat>
    )

    @Serializable
    data class VideoDetails(
        val allowRatings: Boolean,
        val author: String,
        val channelId: String,
        val isLiveContent: Boolean,
        val isPrivate: Boolean,
        val lengthSeconds: String,
        val shortDescription: String,
        val thumbnail: Thumbnail,
        val title: String,
        val videoId: String,
        val viewCount: String
    ) {
        @Serializable
        data class Thumbnail(val thumbnails: List<Thumbnail>) {
            @Serializable
            data class Thumbnail(
                val width: Int,
                val height: Int,
                val url: String,
            )
        }
    }
}