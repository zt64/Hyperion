package dev.zt64.hyperion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.domain.model.RadioOption

sealed interface Destination

enum class BaseDestination(
    val icon: ImageVector,
    override val label: StringResource
) : Destination, RadioOption {
    HOME(Icons.Default.Home, MR.strings.home),
    FEED(Icons.Default.Subscriptions, MR.strings.feed),
    LIBRARY(Icons.Default.VideoLibrary, MR.strings.library)
}

@Parcelize
sealed interface AppDestination : Destination, Parcelable {
    @Parcelize
    data object Search : AppDestination

    @Parcelize
    data class Player(val videoId: String) : AppDestination

    @Parcelize
    data class Channel(val channelId: String) : AppDestination

    @Parcelize
    data class Playlist(val playlistId: String) : AppDestination

    @Parcelize
    data class Tag(val tag: String) : AppDestination

    @Parcelize
    data object AddAccount : AppDestination

    @Parcelize
    data object Notifications : AppDestination

    @Parcelize
    data object Channels : AppDestination

    @Parcelize
    data object History : AppDestination

    @Parcelize
    data object Settings : AppDestination
}