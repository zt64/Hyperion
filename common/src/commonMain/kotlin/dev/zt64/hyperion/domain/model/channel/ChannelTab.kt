package dev.zt64.hyperion.domain.model.channel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.resources.StringResource
import dev.zt64.hyperion.MR
import dev.zt64.innertube.network.dto.browse.ChannelTab

val ChannelTab.title: StringResource
    get() = when (this) {
        ChannelTab.HOME -> MR.strings.home
        ChannelTab.VIDEOS -> MR.strings.videos
        ChannelTab.SHORTS -> MR.strings.shorts
        ChannelTab.LIVE -> MR.strings.live
        ChannelTab.PLAYLISTS -> MR.strings.playlists
        ChannelTab.COMMUNITY -> MR.strings.community
        ChannelTab.STORE -> MR.strings.store
        ChannelTab.CHANNELS -> MR.strings.channels
        ChannelTab.ABOUT -> MR.strings.about
    }

val ChannelTab.imageVector: ImageVector
    get() = when (this) {
        ChannelTab.HOME -> Icons.Default.Home
        ChannelTab.VIDEOS -> Icons.Default.VideoLibrary
        ChannelTab.SHORTS -> Icons.Default.VideoLibrary
        ChannelTab.LIVE -> Icons.Default.VideoLibrary
        ChannelTab.PLAYLISTS -> Icons.Default.ViewList
        ChannelTab.COMMUNITY -> Icons.Default.People
        ChannelTab.STORE -> Icons.Default.ShoppingCart
        ChannelTab.CHANNELS -> Icons.Default.AccountTree
        ChannelTab.ABOUT -> Icons.Default.Info
    }