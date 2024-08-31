package dev.zt64.hyperion.api.model

import com.google.api.client.util.DateTime
import com.google.api.services.youtube.model.Playlist
import com.google.api.services.youtube.model.Video

interface Resource {
    val id: String
}

abstract class Video(ytVideo: Video) : Resource {
    val title: String = ytVideo.snippet.title!!
    val description: String? = ytVideo.snippet.description
    val publishedAt: DateTime = ytVideo.snippet.publishedAt
}

open class Channel(
    final override val id: String,
    val title: String,
    val description: String?,
    val thumbnails: Thumbnails,
    val statistics: Statistics
) : Resource {
    data class Statistics(
        val viewCount: Long,
        val subscriberCount: Long,
        val hiddenSubscriberCount: Boolean,
        val videoCount: Long
    )
}

open class Playlist(
    final override val id: String,
    val title: String,
    val description: String?,
    val channelTitle: String,
    val thumbnails: Thumbnails,
    val publishedAt: DateTime,
    val itemCount: Long
) : Resource {
    constructor(ytPlaylist: Playlist) : this(
        id = ytPlaylist.id,
        title = ytPlaylist.snippet.title!!,
        description = ytPlaylist.snippet.description,
        channelTitle = ytPlaylist.snippet.channelTitle!!,
        thumbnails = Thumbnails(ytPlaylist.snippet.thumbnails),
        publishedAt = ytPlaylist.snippet.publishedAt,
        itemCount = ytPlaylist.contentDetails.itemCount
    )

    val shareUrl = "https://youtube.com/playlist?list=$id"
}