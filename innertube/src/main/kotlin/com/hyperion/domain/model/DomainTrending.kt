package com.hyperion.domain.model

data class DomainTrending(
    override val continuation: String? = null,
    override val items: List<DomainVideoPartial>
) : DomainBrowse<DomainVideoPartial>()