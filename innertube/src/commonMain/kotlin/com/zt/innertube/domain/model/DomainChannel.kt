package com.zt.innertube.domain.model

import com.zt.innertube.network.dto.ApiImageSource

data class DomainChannel(
    val id: String,
    val name: String,
    val description: String?,
    val subscriberText: String?,
    val avatar: String,
    val banner: ApiImageSource?,
    override val items: List<DomainVideoPartial>,
    override val continuation: String?
) : DomainBrowse<DomainVideoPartial>() {
    val shareUrl = "https://www.youtube.com/channel/$id"
}

data class DomainChannelPartial(
    val id: String,
    val name: String? = null,
    val avatarUrl: String? = null,
    val thumbnailUrl: String? = null,
    val subscriptionsText: String? = null,
    val videoCountText: String? = null
) : Entity