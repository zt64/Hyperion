package com.hyperion.domain.model

data class DomainComment(
    val author: DomainChannelPartial,
    val content: String,
    val likeCount: Int,
    val datePosted: String
)