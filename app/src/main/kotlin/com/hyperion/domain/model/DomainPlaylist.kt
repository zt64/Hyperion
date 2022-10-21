package com.hyperion.domain.model

data class DomainPlaylist(
    val id: String,
    val name: String,
    val videoCount: String,
    val channel: DomainChannelPartial,
    val content: Content
) {
    val shareUrl = "https://youtube.com/playlist?list=$id"

    data class Content(
        val videos: List<DomainVideoPartial>,
        val continuation: String?
    )
}