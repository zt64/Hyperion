package com.hyperion.util

import androidx.annotation.StringRes
import com.hyperion.R
import com.hyperion.ui.screen.destinations.*

@get:StringRes
val Destination.title
    get(): Int? = when (this) {
        HomeScreenDestination -> R.string.home
        FeedScreenDestination -> R.string.feed
        LibraryScreenDestination -> R.string.library_screen
        SettingsScreenDestination -> R.string.settings_screen
        else -> null
    }