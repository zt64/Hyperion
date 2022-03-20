package com.hyperion.preferences

import android.os.Environment
import com.hyperion.ui.theme.Theme

enum class VideoCardStyle(val displayName: String) {
    COMPACT("Compact"), LARGE("Large")
}

object Prefs {
    var firstLime by booleanPreference("first_load", true)
    var theme by enumPreference("theme", Theme.SYSTEM)
    var downloadDirectory by stringPreference("download_directory", Environment.DIRECTORY_DOWNLOADS)
    var videoCardStyle by enumPreference("video_card_style", VideoCardStyle.LARGE)
}