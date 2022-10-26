package com.hyperion.domain.model

data class DomainTrending(
    override val items: List<DomainVideoPartial>,
    override val continuation: String? = null
) : DomainBrowse<DomainVideoPartial>()