package dev.zt64.hyperion.api.domain.model

data class DomainShelf(
    val id: String? = null,
    val title: String,
    val description: String,
    val videos: List<DomainVideoPartial>
)