package com.hyperion.domain.model

data class DomainChannel(
    val id: String,
    val name: String,
    val description: String?,
    val subscriberText: String?,
    val avatar: String,
    val banner: String?,
    val videos: List<DomainVideoPartial>
) {
    val shareUrl = "https://www.youtube.com/channel/$id"
}

data class DomainChannelPartial(
    val id: String,
    val name: String? = null,
    val avatarUrl: String? = null,
    val subscriberText: String? = null
)