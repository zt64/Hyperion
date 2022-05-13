package com.hyperion.domain.model

data class DomainTrending(
    val continuation: String? = null,
    val videos: List<DomainVideoPartial>
)