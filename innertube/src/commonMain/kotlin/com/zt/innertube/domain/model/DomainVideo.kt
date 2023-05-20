package com.zt.innertube.domain.model

import com.zt.innertube.network.service.InnerTubeService

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
    val publishedTimeText: String?, // null if live
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

    private companion object {
        private const val SEPARATOR = " • "
    }
}