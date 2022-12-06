package com.zt.innertube.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiPlayer(
    val streamingData: StreamingData,
    val videoDetails: VideoDetails
) {
    @Serializable
    data class StreamingData(
        val formats: List<ApiFormat> = emptyList(),
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
        val thumbnail: ApiThumbnail,
        val title: String,
        val videoId: String,
        val viewCount: String? = null
    )
}