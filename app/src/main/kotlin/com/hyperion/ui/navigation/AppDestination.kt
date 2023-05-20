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
enum class BaseDestination : AppDestination {
    HOME,
    FEED,
    LIBRARY;

    @Stable
    val icon: ImageVector
        get() = when (this) {
            HOME -> Icons.Default.Home
            FEED -> Icons.Default.Subscriptions
            LIBRARY -> Icons.Default.VideoLibrary
        }

    @Stable
    val label: Int
        @StringRes
        get() = when (this) {
            HOME -> R.string.home
            FEED -> R.string.feed
            LIBRARY -> R.string.library
        }
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