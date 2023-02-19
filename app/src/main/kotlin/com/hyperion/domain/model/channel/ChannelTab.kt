package com.hyperion.domain.model.channel

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.hyperion.R

@Immutable
enum class ChannelTab(
    @StringRes
    val title: Int,
    val imageVector: ImageVector
) {
    HOME(R.string.home, Icons.Default.Home),
    VIDEOS(R.string.videos, Icons.Default.VideoLibrary),
    PLAYLISTS(R.string.playlists, Icons.Default.ViewList),
    COMMUNITY(R.string.community, Icons.Default.People),
    CHANNELS(R.string.channels, Icons.Default.AccountTree),
    ABOUT(R.string.about, Icons.Default.Info)
}