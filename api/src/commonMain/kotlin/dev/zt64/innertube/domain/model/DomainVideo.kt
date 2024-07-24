package dev.zt64.innertube.domain.model

import dev.zt64.innertube.network.service.InnerTubeService

data class DomainVideo(
    val id: String,
    val title: String,
    val viewCount: String,
    val uploadDate: String,
    val description: String,
    val likesText: String,
    val dislikesText: String,
    val formats: List<DomainFormat>,
    val author: DomainChannelPartial,
    val badges: List<String>,
    val chapters: List<DomainChapter>
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