package com.zt.innertube.domain.model

data class DomainSearch(
    override val items: List<Entity>,
    override val continuation: String?
) : DomainBrowse<Entity>()