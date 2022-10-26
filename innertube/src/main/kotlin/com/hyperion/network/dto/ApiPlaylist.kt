package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.SectionListRenderer
import kotlinx.serialization.Serializable

@Serializable
data class ApiPlaylist(
    val header: Header,
    override val contents: Contents<SectionContent>
) : ApiBrowse() {
    @Serializable
    data class Header(val playlistHeaderRenderer: PlaylistHeaderRenderer) {
        @Serializable
        data class PlaylistHeaderRenderer(
            val briefStats: List<ApiText> = emptyList(),
            val isEditable: Boolean,
            val numVideosText: ApiText,
            val ownerEndpoint: ApiNavigationEndpoint,
            val ownerText: ApiText,
            val playlistId: String,
            val title: ApiText,
            val viewCountText: ApiText
        )
    }

    @Serializable
    data class SectionContent(val playlistVideoListRenderer: SectionListRenderer<Content>) {
        @Serializable
        data class Content(val playlistVideoRenderer: VideoRenderer? = null) {
            @Serializable
            data class VideoRenderer(
                val index: ApiText,
                val isPlayable: Boolean,
                val lengthSeconds: String,
                val lengthText: ApiText,
                val navigationEndpoint: NavigationEndpoint,
                val shortBylineText: ApiText,
                val thumbnail: ApiThumbnail,
                val thumbnailOverlays: List<ThumbnailOverlay>,
                val title: ApiText,
                val videoId: String
            ) {
                @Serializable
                data class NavigationEndpoint(val watchEndpoint: WatchEndpoint) {
                    @Serializable
                    data class WatchEndpoint(
                        val params: String,
                        val playlistId: String,
                        val videoId: String
                    )
                }

                @Serializable
                data class ThumbnailOverlay(val thumbnailOverlayTimeStatusRenderer: ThumbnailOverlayTimeStatusRenderer) {
                    @Serializable
                    data class ThumbnailOverlayTimeStatusRenderer(val text: ApiText)
                }
            }
        }

    }
}

@Serializable
data class ApiPlaylistContinuation(val continuationContents: ContinuationContents) {
    @Serializable
    data class ContinuationContents(val playlistVideoListContinuation: SectionListRenderer<ApiPlaylist.SectionContent.Content>)
}