package com.hyperion.domain.manager

import android.content.SharedPreferences
import android.os.Environment
import com.hyperion.domain.manager.base.BasePreferenceManager
import com.hyperion.ui.component.NavigationDestination
import com.hyperion.ui.theme.Theme

class PreferencesManager(
    sharedPreferences: SharedPreferences
) : BasePreferenceManager(sharedPreferences) {
    var theme by enumPreference("theme", Theme.SYSTEM)
    var materialYou by booleanPreference("material_you", true)
    var midnightMode by booleanPreference("midnight_mode", false)
    var compactCard by booleanPreference("compact_card", false)
    var startScreen by enumPreference("start_screen", NavigationDestination.Home)
    var pictureInPicture by booleanPreference("pip", true)
    var downloadDirectory by stringPreference("download_directory", Environment.DIRECTORY_DOWNLOADS)
    var timestampScale by floatPreference("timestamp_scale", 1f)
}