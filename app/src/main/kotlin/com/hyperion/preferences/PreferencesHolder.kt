package com.hyperion.preferences

import android.os.Environment
import com.hyperion.ui.components.NavigationDestination
import com.hyperion.ui.theme.Theme

enum class VideoCardStyle(val displayName: String) {
    COMPACT("Compact"), LARGE("Large")
}

object Prefs {
    var theme by enumPreference("theme", Theme.SYSTEM)
    var downloadDirectory by stringPreference("download_directory", Environment.DIRECTORY_DOWNLOADS)
    var videoCardStyle by enumPreference("video_card_style", VideoCardStyle.LARGE)
    var startScreen by enumPreference("start_screen", NavigationDestination.Home)
    var materialYou by booleanPreference("material_you", true)
    var blackBackground by booleanPreference("black_background", false)
}