package com.hyperion.ui.navigation

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import com.hyperion.R
import com.hyperion.ui.component.setting.RadioOption
import kotlinx.parcelize.Parcelize

sealed interface Destination

enum class BaseDestination(
    val icon: ImageVector,
    @StringRes
    override val label: Int
) : Destination, RadioOption {
    HOME(Icons.Default.Home, R.string.home),
    FEED(Icons.Default.Subscriptions, R.string.feed),
    LIBRARY(Icons.Default.VideoLibrary, R.string.library)
}

@Parcelize
sealed interface AppDestination : Destination, Parcelable {
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
    object Notifications : AppDestination

    @Parcelize
    object Channels : AppDestination

    @Parcelize
    object History : AppDestination

    @Parcelize
    object Settings : AppDestination
}