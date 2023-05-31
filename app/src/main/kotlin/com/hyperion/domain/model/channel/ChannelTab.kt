package com.hyperion.domain.model.channel

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.hyperion.R
import com.zt.innertube.network.dto.browse.ChannelTab

val ChannelTab.title: Int
    @StringRes
    get() = when (this) {
        ChannelTab.HOME -> R.string.home
        ChannelTab.VIDEOS -> R.string.videos
        ChannelTab.SHORTS -> R.string.shorts
        ChannelTab.LIVE -> R.string.live
        ChannelTab.PLAYLISTS -> R.string.playlists
        ChannelTab.COMMUNITY -> R.string.community
        ChannelTab.STORE -> R.string.store
        ChannelTab.CHANNELS -> R.string.channels
        ChannelTab.ABOUT -> R.string.about
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