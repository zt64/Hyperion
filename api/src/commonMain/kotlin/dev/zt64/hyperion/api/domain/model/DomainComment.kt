package dev.zt64.hyperion.api.domain.model

data class DomainComment(
    val id: String,
    val author: DomainChannelPartial,
    val content: String,
    val likeCount: Int,
    val datePosted: String,
    val replies: List<DomainComment>
)