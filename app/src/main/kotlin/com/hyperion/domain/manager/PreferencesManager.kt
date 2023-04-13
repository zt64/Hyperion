package com.hyperion.domain.manager

import android.content.SharedPreferences
import android.os.Environment
import com.hyperion.domain.manager.base.BasePreferenceManager
import com.hyperion.ui.navigation.BaseDestination
import com.hyperion.ui.theme.Theme

class PreferencesManager(sharedPreferences: SharedPreferences) : BasePreferenceManager(sharedPreferences) {
    var theme by preference("theme", Theme.SYSTEM)
    var dynamicColor by preference("dynamic_color", true)
    var compactCard by preference("compact_card", false)
    var miniPlayer by preference("mini_player", false)
    var startScreen by preference("start_screen", BaseDestination.HOME)
    var pictureInPicture by preference("pip", false)
    var showDownloadButton by preference("show_download_button", true)
    var showRelatedVideos by preference("show_related_videos", true)
    var downloadDirectory by preference("download_directory", Environment.DIRECTORY_DOWNLOADS)
    var timestampScale by preference("timestamp_scale", 1f)
    var hideNavItemLabel by preference("hide_nav_item_label", false)

    // Gestures
    var swipeToSeek by preference("swipe_to_seek", true)
    var swipeToSeekSensitivity by preference("swipe_to_seek_sensitivity", 1f)
    var brightnessGesture by preference("brightness_gesture", true)
    var volumeGesture by preference("volume_gesture", true)
    var doubleTapToSeek by preference("double_tap_to_seek", true)
    var doubleTapToSeekDuration by preference("double_tap_to_seek_duration", 10)

    var sponsorBlockEnabled by preference("sponsor_block_enabled", true)
    var sponsorBlockSkipNoticeDuration by preference("sponsor_block_skip_notice_duration", 5)
    var sponsorBlockUserIdPrivate by preference("sponsor_block_user_id", "")
    var sponsorBlockUserIdPublic by preference("sponsor_block_user_id_public", "")
}