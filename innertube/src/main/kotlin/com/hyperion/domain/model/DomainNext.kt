package com.hyperion.domain.model

data class DomainNext(
    val viewCount: String,
    val uploadDate: String,
    val channelAvatarUrl: String,
    val likesText: String,
    val subscribersText: String? = null,
    val comments: Comments,
    val relatedVideos: RelatedVideos,
    val badges: List<String>
) {
    data class Comments(
        val comments: List<DomainComment>,
        val continuation: String? = null
    )

    data class RelatedVideos(
        val videos: List<DomainVideoPartial>,
        val continuation: String? = null
    )
}