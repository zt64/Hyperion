package dev.zt64.innertube.network.body

import dev.zt64.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
internal data class PlayerBody(
    override val context: InnerTubeContext,
    val videoId: String,
    val contentCheckOk: Boolean? = true,
    val racyCheckOk: Boolean? = true,
    val mwebCapabilities: MwebCapabilities? = MwebCapabilities(),
    val playbackContext: PlaybackContext? = PlaybackContext(videoId),
    val params: String? = "8AEB"
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
            val html5Preference: String = "HTML5_PREF_WANTS",
            val lactMilliseconds: String = "-1",
            val referer: String = "https://m.youtube.com/",
            val signatureTimestamp: Int = 19473,
            val splay: Boolean = false,
            val vis: Int = 0
        ) {
            constructor(videoId: String) : this(currentUrl = "/watch?v=$videoId")
        }
    }
}