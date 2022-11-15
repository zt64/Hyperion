package com.hyperion.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface AppDestination : Parcelable {
    @Parcelize
    object Root : AppDestination

    @Parcelize
    object Search : AppDestination

    @Parcelize
    class Player(val videoId: String) : AppDestination

    @Parcelize
    class Channel(val channelId: String) : AppDestination

    @Parcelize
    class Playlist(val playlistId: String) : AppDestination

    @Parcelize
    class Tag(val tag: String) : AppDestination

    @Parcelize
    object Settings : AppDestination
}