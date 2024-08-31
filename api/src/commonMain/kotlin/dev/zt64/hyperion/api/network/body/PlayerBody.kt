package dev.zt64.hyperion.api.network.body

import dev.zt64.hyperion.api.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class PlayerBody(
    override val context: InnerTubeContext,
    val videoId: String,
    val contentCheckOk: Boolean? = true,
    val racyCheckOk: Boolean? = true,
    // val mwebCapabilities: MwebCapabilities? = MwebCapabilities(),
    // val playbackContext: PlaybackContext? = PlaybackContext(videoId),
    val params: String? = "2AMB"
) : IBody {
    @Serializable
    data class MwebCapabilities(val mobileClientSupportsLivestream: Boolean = true)

    @Serializable
    data class PlaybackContext(val contentPlaybackContext: ContentPlaybackContext) {
        constructor(videoId: String) : this(ContentPlaybackContext(videoId))

        @Serializable
        data class ContentPlaybackContext(
            val autoCaptionsDefaultOn: Boolean = false,
            val autonavState: String = "STATE_NONE",
            val currentUrl: String,
            val html5Preference: String = "HTML5_PREF_WANTS"
        ) {
            constructor(videoId: String) : this(currentUrl = "/watch?v=$videoId")
        }
    }
}