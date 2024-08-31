package dev.zt64.hyperion.api.domain.model

import dev.zt64.hyperion.api.network.service.InnerTubeService

data class DomainVideo(
    val id: String,
    val title: String,
    val viewCount: String,
    val uploadDate: String,
    val description: String,
    val likesText: String,
    val author: DomainChannelPartial,
    val badges: List<String>,
    val chapters: List<DomainChapter>,
    val streamingData: DomainStreamingData
) {
    val shareUrl = "https://youtu.be/$id"
}

data class DomainVideoPartial(
    val id: String,
    val title: String,
    val viewCount: String?,
    // null if live
    val publishedTimeText: String?,
    val ownerText: String? = null,
    val timestamp: String? = null,
    val channel: DomainChannelPartial? = null
) : Entity {
    val thumbnailUrl = InnerTubeService.getVideoThumbnail(id)
    val isLive = publishedTimeText == null
    val subtitle = listOfNotNull(
        ownerText,
        publishedTimeText,
        viewCount
    ).joinToString(SEPARATOR)
    val shareUrl = "https://youtu.be/$id"

    private companion object {
        private const val SEPARATOR = " • "
    }
}