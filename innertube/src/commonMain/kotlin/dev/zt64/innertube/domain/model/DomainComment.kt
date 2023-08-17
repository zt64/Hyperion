package dev.zt64.innertube.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class DomainComment(
    val id: String,
    val author: DomainChannelPartial,
    val content: String,
    val likeCount: Int,
    val datePosted: String,
    val replies: List<DomainComment>
)