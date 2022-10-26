package com.hyperion.domain.model

data class DomainSearch(
    override val items: List<Entity>,
    override val continuation: String?
) : DomainBrowse<Entity>()