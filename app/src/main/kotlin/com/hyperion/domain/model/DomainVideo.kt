package com.hyperion.domain.model

import com.hyperion.network.service.InnerTubeService

data class DomainVideo(
    val id: String,
    val title: String,
    val viewCount: String,
    val uploadDate: String,
    val description: String = "",
    val likesText: String,
    val dislikesText: String,
    val streams: List<DomainStream>,
    val author: DomainChannelPartial
) {
    val shareUrl = "https://youtu.be/$id"
}

data class DomainVideoPartial(
    val id: String,
    val title: String,
    val subtitle: String,
    val timestamp: String? = null,
    val author: DomainChannelPartial? = null
) {
    val thumbnailUrl = InnerTubeService.getVideoThumbnail(id)
}