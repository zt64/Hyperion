package com.hyperion.domain.model

data class DomainSearch(
    val continuation: String?,
    val items: List<Result>
) {
    sealed class Result {
        data class Video(
            val id: String,
            val title: String,
            val subtitle: String,
            val timestamp: String? = null,
            val thumbnailUrl: String,
            val author: DomainChannelPartial? = null
        ) : Result()

        data class Channel(
            val id: String,
            val name: String,
            val thumbnailUrl: String,
            val subscriptionsText: String? = null,
            val videoCountText: String? = null
        ) : Result()

        data class Playlist(
            val id: String,
            val title: String,
            val thumbnailUrl: String,
            val channelName: String,
            val videoCountText: String
        ) : Result()
    }
}