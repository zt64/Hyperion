package com.hyperion.domain.model

data class DomainRecommended(
    val continuation: String? = null,
    val videos: List<DomainVideoPartial>
)