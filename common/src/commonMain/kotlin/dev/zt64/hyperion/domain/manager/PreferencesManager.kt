package dev.zt64.hyperion.domain.manager

import com.russhwolf.settings.Settings
import dev.zt64.hyperion.Platform
import dev.zt64.hyperion.domain.manager.base.BasePreferenceManager
import dev.zt64.hyperion.ui.theme.Theme
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class PreferencesManager(settings: Settings) : BasePreferenceManager(settings) {
    var visitorData by preference("visitor_data")
    var deviceId by preference("device_id")

    var theme by preference("theme", Theme.SYSTEM)
    var dynamicColor by preference("dynamic_color", true)
    var miniPlayer by preference("mini_player", false)
    var pictureInPicture by preference("pip", false)
    var showDownloadButton by preference("show_download_button", true)
    var showRelatedVideos by preference("show_related_videos", true)
    var downloadDirectory by preference(
        "download_directory",
        try {
            Platform.getDownloadsDir().path
        } catch (e: Exception) {
            ""
        }
    )
    var timestampScale by preference("timestamp_scale", 1f)
    var hideNavItemLabel by preference("hide_nav_item_label", false)

    // Gestures
    var swipeToSeek by preference("swipe_to_seek", true)
    var swipeToSeekSensitivity by preference("swipe_to_seek_sensitivity", 1f)
    var brightnessGesture by preference("brightness_gesture", true)
    var volumeGesture by preference("volume_gesture", true)
    var doubleTapToSeek by preference("double_tap_to_seek", true)
    var doubleTapToSeekDuration by preference("double_tap_to_seek_duration", 10)

    // Video
    var videoQuality by preference("video_quality", "auto")
    var videoFormat by preference("video_format", "mp4")
    var videoCodec by preference("video_codec", "avc1")
    var videoResolution by preference("video_resolution", "auto")
    var videoFrameRate by preference("video_frame_rate", "auto")

    var sponsorBlockEnabled by preference("sponsor_block_enabled", true)
    var sponsorBlockApiUrl by preference("sponsor_block_api_url", "https://sponsor.ajay.app")
    var sponsorBlockSkipNoticeDuration by preference("sponsor_block_skip_notice_duration", 5)
    var sponsorBlockSkipTracking by preference("sponsor_block_skip_tracking", true)
    var sponsorBlockUserIdPrivate by preference("sponsor_block_user_id")
    var sponsorBlockUserIdPublic by preference("sponsor_block_user_id_public")
}

internal class PreferencesManagerImpl : PreferencesManager(Settings())
internal class PreferencesManagerPreviewImpl : PreferencesManager(Settings())

private inline fun <reified E : Enum<E>> Settings.enum(
    key: String? = null,
    defaultValue: E
): ReadWriteProperty<Any, E> = object : ReadWriteProperty<Any, E> {
    override fun getValue(thisRef: Any, property: KProperty<*>): E {
        return enumValues<E>()[getInt(key ?: property.name, defaultValue.ordinal)]
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: E) {
        putInt(key ?: property.name, value.ordinal)
    }
}
