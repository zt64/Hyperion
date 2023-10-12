package dev.zt64.hyperion.domain.manager

import com.russhwolf.settings.*
import dev.zt64.hyperion.Platform
import dev.zt64.hyperion.ui.navigation.BaseDestination
import dev.zt64.hyperion.ui.theme.Theme
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class PreferencesManager : Settings {
    var visitorData by nullableString("visitor_data")
    var deviceId by nullableString("device_id")

    var theme by enum("theme", Theme.SYSTEM)
    var dynamicColor by boolean("dynamic_color", true)
    var miniPlayer by boolean("mini_player", false)
    var startScreen by enum("start_screen", BaseDestination.HOME)
    var pictureInPicture by boolean("pip", false)
    var showDownloadButton by boolean("show_download_button", true)
    var showRelatedVideos by boolean("show_related_videos", true)
    var downloadDirectory by string(
        "download_directory",
        try {
            Platform.getDownloadsDir().path
        } catch (e: Exception) {
            ""
        }
    )
    var timestampScale by float("timestamp_scale", 1f)
    var hideNavItemLabel by boolean("hide_nav_item_label", false)

    // Gestures
    var swipeToSeek by boolean("swipe_to_seek", true)
    var swipeToSeekSensitivity by float("swipe_to_seek_sensitivity", 1f)
    var brightnessGesture by boolean("brightness_gesture", true)
    var volumeGesture by boolean("volume_gesture", true)
    var doubleTapToSeek by boolean("double_tap_to_seek", true)
    var doubleTapToSeekDuration by int("double_tap_to_seek_duration", 10)

    // Video
    var videoQuality by string("video_quality", "auto")
    var videoFormat by string("video_format", "mp4")
    var videoCodec by string("video_codec", "avc1")
    var videoResolution by string("video_resolution", "auto")
    var videoFrameRate by string("video_frame_rate", "auto")

    var sponsorBlockEnabled by boolean("sponsor_block_enabled", true)
    var sponsorBlockApiUrl by string("sponsor_block_api_url", "https://sponsor.ajay.app")
    var sponsorBlockSkipNoticeDuration by int("sponsor_block_skip_notice_duration", 5)
    var sponsorBlockSkipTracking by boolean("sponsor_block_skip_tracking", true)
    var sponsorBlockUserIdPrivate by nullableString("sponsor_block_user_id")
    var sponsorBlockUserIdPublic by nullableString("sponsor_block_user_id_public")
}

internal class PreferencesManagerImpl : PreferencesManager(), Settings by Settings()
internal class PreferencesManagerPreviewImpl : PreferencesManager(), Settings by MapSettings()

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
