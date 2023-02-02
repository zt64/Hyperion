package com.hyperion.domain.manager

import android.content.SharedPreferences
import android.os.Environment
import com.hyperion.domain.manager.base.BasePreferenceManager
import com.hyperion.ui.navigation.BaseDestination
import com.hyperion.ui.theme.Theme

class PreferencesManager(sharedPreferences: SharedPreferences) : BasePreferenceManager(sharedPreferences) {
    var theme by enumPreference("theme", Theme.SYSTEM)
    var dynamicColor by booleanPreference("dynamic_color", true)
    var compactCard by booleanPreference("compact_card", false)
    var startScreen by enumPreference("start_screen", BaseDestination.HOME)
    var pictureInPicture by booleanPreference("pip", false)
    var showDownloadButton by booleanPreference("show_download_button", true)
    var showRelatedVideos by booleanPreference("show_related_videos", true)
    var downloadDirectory by stringPreference("download_directory", Environment.DIRECTORY_DOWNLOADS)
    var timestampScale by floatPreference("timestamp_scale", 1f)
    var hideNavItemLabel by booleanPreference("hide_nav_item_label", false)
}