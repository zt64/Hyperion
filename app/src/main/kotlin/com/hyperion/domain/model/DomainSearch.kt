package com.hyperion.domain.model

data class DomainSearch(
    val continuation: String?,
    val items: List<Entity>
)