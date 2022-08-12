package com.hyperion.domain.model

import com.hyperion.network.service.InnerTubeService

data class DomainSearch(
    val continuation: String?,
    val items: List<Result>
) {
    sealed interface Result {
        val id: String

        data class Video(
            override val id: String,
            val title: String,
            val subtitle: String,
            val timestamp: String?,
            val author: DomainChannelPartial?
        ) : Result {
            val thumbnailUrl = InnerTubeService.getVideoThumbnail(id)
        }

        data class Channel(
            override val id: String,
            val name: String,
            val thumbnailUrl: String,
            val subscriptionsText: String?,
            val videoCountText: String?
        ) : Result

        data class Playlist(
            override val id: String,
            val title: String,
            val thumbnailUrl: String,
            val channelName: String,
            val videoCountText: String
        ) : Result
    }
}