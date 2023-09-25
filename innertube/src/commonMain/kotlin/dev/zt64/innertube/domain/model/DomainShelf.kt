package dev.zt64.innertube.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class DomainShelf(
    val id: String? = null,
    val title: String,
    val description: String,
    val videos: List<DomainVideoPartial>
)