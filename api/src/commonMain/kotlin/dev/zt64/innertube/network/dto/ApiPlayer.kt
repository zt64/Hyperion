package dev.zt64.innertube.network.dto

import dev.zt64.innertube.domain.model.DomainFormat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiPlayer(
    val streamingData: StreamingData,
    val videoDetails: VideoDetails,
    val playabilityStatus: PlayabilityStatus
) {
    @Serializable
    data class StreamingData(val adaptiveFormats: List<DomainFormat>)

    @Serializable
    data class VideoDetails(
        val allowRatings: Boolean,
        val author: String,
        val channelId: String,
        val isLiveContent: Boolean,
        val isPrivate: Boolean,
        val lengthSeconds: String,
        val shortDescription: String,
        val thumbnail: ApiImage,
        val title: String,
        val videoId: String,
        val viewCount: String? = null
    )

    @Serializable
    data class PlayabilityStatus(val status: Status)
}

@Serializable
internal enum class Status {
    @SerialName("OK")
    OK,

    @SerialName("ERROR")
    ERROR,

    @SerialName("UNPLAYABLE")
    UNPLAYABLE
}