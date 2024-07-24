package dev.zt64.innertube.domain.model

data class DomainMix(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnailUrl: String,
    override val items: List<DomainVideoPartial>,
    override val continuation: String?
) : DomainBrowse<DomainVideoPartial>()

data class DomainMixPartial(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnailUrl: String
) : Entity