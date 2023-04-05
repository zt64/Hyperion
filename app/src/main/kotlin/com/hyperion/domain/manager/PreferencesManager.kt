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
    var miniPlayer by booleanPreference("mini_player", false)
    var startScreen by enumPreference("start_screen", BaseDestination.HOME)
    var pictureInPicture by booleanPreference("pip", false)
    var showDownloadButton by booleanPreference("show_download_button", true)
    var showRelatedVideos by booleanPreference("show_related_videos", true)
    var downloadDirectory by stringPreference("download_directory", Environment.DIRECTORY_DOWNLOADS)
    var timestampScale by floatPreference("timestamp_scale", 1f)
    var hideNavItemLabel by booleanPreference("hide_nav_item_label", false)

    // Gestures
    var swipeToSeek by booleanPreference("swipe_to_seek", true)
    var swipeToSeekSensitivity by floatPreference("swipe_to_seek_sensitivity", 1f)
    var brightnessGesture by booleanPreference("brightness_gesture", true)
    var volumeGesture by booleanPreference("volume_gesture", true)
    var doubleTapToSeek by booleanPreference("double_tap_to_seek", true)
    var doubleTapToSeekDuration by intPreference("double_tap_to_seek_duration", 10)

    var sponsorBlockEnabled by booleanPreference("sponsor_block_enabled", true)
    var sponsorBlockSkipNoticeDuration by intPreference("sponsor_block_skip_notice_duration", 5)
    var sponsorBlockUserIdPrivate by stringPreference("sponsor_block_user_id", "")
    var sponsorBlockUserIdPublic by stringPreference("sponsor_block_user_id_public", "")
}