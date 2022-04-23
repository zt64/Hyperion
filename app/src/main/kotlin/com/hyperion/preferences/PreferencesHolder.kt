package com.hyperion.preferences

import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.ui.components.NavigationDestination
import com.hyperion.ui.theme.Theme

enum class VideoCardStyle {
    COMPACT,
    LARGE;

    @Composable
    fun toDisplayName(): String = when (this) {
        COMPACT -> stringResource(R.string.compact)
        LARGE -> stringResource(R.string.large)
    }
}

object Prefs {
    var firstLaunch by booleanPreference("first_launch", true)
    var theme by enumPreference("theme", Theme.SYSTEM)
    var downloadDirectory by stringPreference("download_directory", Environment.DIRECTORY_DOWNLOADS)
    var videoCardStyle by enumPreference("video_card_style", VideoCardStyle.LARGE)
    var startScreen by enumPreference("start_screen", NavigationDestination.Home)
}