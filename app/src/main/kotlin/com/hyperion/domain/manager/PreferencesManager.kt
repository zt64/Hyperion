package com.hyperion.domain.manager

import android.content.SharedPreferences
import android.os.Environment
import com.hyperion.domain.manager.base.BasePreferenceManager
import com.hyperion.ui.components.NavigationDestination
import com.hyperion.ui.theme.Theme

class PreferencesManager(
    sharedPreferences: SharedPreferences
) : BasePreferenceManager(sharedPreferences) {
    var theme by enumPreference(THEME_KEY, THEME_DEFAULT)
    var materialYou by booleanPreference(MATERIAL_YOU_KEY, MATERIAL_YOU_DEFAULT)
    var midnightMode by booleanPreference(MIDNIGHT_MODE_KEY, MIDNIGHT_MODE_DEFAULT)
    var compactCard by booleanPreference(COMPACT_CARD_KEY, COMPACT_CARD_DEFAULT)
    var startScreen by enumPreference(START_SCREEN_KEY, START_SCREEN_DEFAULT)
    var pictureInPicture by booleanPreference(PIP_KEY, PIP_DEFAULT)
    var downloadDirectory by stringPreference(DOWNLOAD_DIRECTORY_KEY, DOWNLOAD_DIRECTORY_DEFAULT)

    companion object {
        private val THEME_KEY = "theme"
        private val THEME_DEFAULT = Theme.SYSTEM

        private val MATERIAL_YOU_KEY = "material_you"
        private val MATERIAL_YOU_DEFAULT = true

        private val MIDNIGHT_MODE_KEY = "midnight_mode"
        private val MIDNIGHT_MODE_DEFAULT = false

        private val COMPACT_CARD_KEY = "compact_card"
        private val COMPACT_CARD_DEFAULT = false

        private val START_SCREEN_KEY = "start_screen"
        private val START_SCREEN_DEFAULT = NavigationDestination.Home

        private val PIP_KEY = "pip"
        private val PIP_DEFAULT = true

        private val DOWNLOAD_DIRECTORY_KEY = "download_directory"
        private val DOWNLOAD_DIRECTORY_DEFAULT = Environment.DIRECTORY_DOWNLOADS
    }
}