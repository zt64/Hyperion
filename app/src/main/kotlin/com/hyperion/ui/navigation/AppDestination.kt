package com.hyperion.ui.navigation

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import com.hyperion.R
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
enum class BaseDestination(
    val icon: @RawValue ImageVector,
    @StringRes val label: Int
) : AppDestination {
    HOME(Icons.Default.Home, R.string.home),
    FEED(Icons.Default.Subscriptions, R.string.feed),
    LIBRARY(Icons.Default.VideoLibrary, R.string.library)
}

@Parcelize
sealed interface AppDestination : Parcelable {
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
    object AddAccount : AppDestination

    @Parcelize
    object Settings : AppDestination
}