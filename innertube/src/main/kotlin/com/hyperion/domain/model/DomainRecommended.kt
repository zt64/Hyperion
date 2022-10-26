package com.hyperion.domain.model

class DomainRecommended(
    override val items: List<DomainVideoPartial>,
    override val continuation: String?
) : DomainBrowse<DomainVideoPartial>()