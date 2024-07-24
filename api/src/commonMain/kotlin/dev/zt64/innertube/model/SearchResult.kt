package dev.zt64.innertube.model

import com.google.api.client.util.DateTime
import com.google.api.services.youtube.model.SearchResult as YtSearchResult

open class SearchResult private constructor(open val id: String? = null, open val etag: String) {
    internal companion object {
        internal fun fromYt(searchResult: YtSearchResult): SearchResult =
            when (searchResult.id.kind) {
                "youtube#video" -> Video(searchResult)
                "youtube#channel" -> Channel(searchResult)
                "youtube#playlist" -> Playlist(searchResult)
                else -> SearchResult(etag = searchResult.etag)
            }
    }

    data class Video internal constructor(
        override val id: String,
        override val etag: String,
        val liveBroadcastContent: LiveBroadcast,
        val thumbnails: Thumbnails
    ) : SearchResult(id, etag) {
        internal constructor(searchResult: YtSearchResult) : this(
            id = searchResult.id.videoId!!,
            etag = searchResult.etag,
            liveBroadcastContent =
                LiveBroadcast.valueOf2(
                    searchResult.snippet.liveBroadcastContent
                ),
            thumbnails = Thumbnails(searchResult.snippet.thumbnails)
        )
    }

    data class Channel internal constructor(
        override val id: String,
        override val etag: String,
        val publishedAt: DateTime,
        val liveBroadcastContent: LiveBroadcast
    ) : SearchResult(id, etag) {
        internal constructor(searchResult: YtSearchResult) : this(
            id = searchResult.id.channelId!!,
            etag = searchResult.etag!!,
            publishedAt = searchResult.snippet.publishedAt,
            liveBroadcastContent =
                LiveBroadcast.valueOf2(
                    searchResult.snippet.liveBroadcastContent
                )
        )
    }

    data class Playlist internal constructor(override val id: String, override val etag: String) :
        SearchResult(id, etag) {
            internal constructor(searchResult: YtSearchResult) : this(
                id = searchResult.id.playlistId!!,
                etag = searchResult.etag
            )
        }
}

enum class LiveBroadcast {
    LIVE,
    UPCOMING,
    NONE
    ;

    internal companion object {
        fun valueOf2(name: String): LiveBroadcast = when (name) {
            "live" -> LIVE
            "upcoming" -> UPCOMING
            else -> NONE
        }
    }
}