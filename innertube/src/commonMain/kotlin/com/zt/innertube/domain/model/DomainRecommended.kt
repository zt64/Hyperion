package com.zt.innertube.domain.model

data class DomainRecommended(
    override val items: List<DomainVideoPartial>,
    override val continuation: String?
) : DomainBrowse<DomainVideoPartial>()