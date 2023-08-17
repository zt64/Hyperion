package dev.zt64.innertube.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class DomainTag(
    val name: String,
    val subtitle: String,
    override val items: List<DomainVideoPartial>,
    override val continuation: String?
) : DomainBrowse<DomainVideoPartial>()

@Immutable
data class DomainTagPartial(
    val name: String,
    val videosCount: String?,
    val channelsCount: String?,
    val backgroundColor: Long
) : Entity