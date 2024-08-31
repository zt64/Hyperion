package dev.zt64.hyperion.api.domain.model

data class DomainTag(
    val name: String,
    val subtitle: String,
    override val items: List<DomainVideoPartial>,
    override val continuation: String?
) : DomainBrowse<DomainVideoPartial>()

data class DomainTagPartial(
    val name: String,
    val videosCount: String?,
    val channelsCount: String?,
    val backgroundColor: Long
) : Entity