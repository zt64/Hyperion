package dev.zt64.innertube.domain.model

import dev.zt64.innertube.network.dto.ApiImageSource
import dev.zt64.innertube.network.dto.browse.ChannelTab

data class DomainChannel(
    val id: String,
    val name: String,
    val description: String?,
    val subscriberText: String?,
    val avatar: String,
    val banner: ApiImageSource?,
    val tabs: List<ChannelTab>
) {
    val shareUrl = "https://www.youtube.com/channel/$id"
}

data class DomainChannelPartial(
    val id: String,
    val name: String? = null,
    val avatarUrl: String? = null,
    val subscriptionsText: String? = null,
    val videoCountText: String? = null
) : Entity