package com.zt.innertube.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class DomainMix(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnailUrl: String,
    override val items: List<DomainVideoPartial>,
    override val continuation: String?,
) : DomainBrowse<DomainVideoPartial>()

@Immutable
data class DomainMixPartial(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnailUrl: String
) : Entity