package com.hyperion.domain.model

data class DomainVideo(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String = "",
    val likesText: String,
    val dislikes: Int,
    val views: Int,
    val streams: List<DomainStream>,
    val author: DomainChannelPartial
)

data class DomainVideoPartial(
    val id: String,
    val title: String,
    val subtitle: String,
    val timestamp: String? = null,
    val thumbnailUrl: String,
    val author: DomainChannelPartial? = null
)