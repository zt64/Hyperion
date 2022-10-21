package com.hyperion.domain.model

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
            val channel: DomainChannelPartial?
        ) : Result

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
            val subtitle: String,
            val videoCountText: String,
            val thumbnailUrl: String
        ) : Result

        data class Mix(
            override val id: String,
            val title: String,
            val subtitle: String,
            val thumbnailUrl: String
        ) : Result
    }
}