package dev.zt64.innertube.model

import com.google.api.services.youtube.model.PlaylistItem
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class PlaylistItem internal constructor(
    val id: String,
    val title: String,
    val channelTitle: String,
    val duration: Duration,
    val thumbnails: Thumbnails
) {
    internal constructor(ytPlaylistItem: PlaylistItem) : this(
        id = ytPlaylistItem.id,
        title = ytPlaylistItem.snippet.title,
        channelTitle = ytPlaylistItem.snippet.channelTitle,
        duration =
            ytPlaylistItem
                .contentDetails
                .endAt
                .toInt()
                .seconds,
        thumbnails = Thumbnails(ytPlaylistItem.snippet.thumbnails)
    )
}