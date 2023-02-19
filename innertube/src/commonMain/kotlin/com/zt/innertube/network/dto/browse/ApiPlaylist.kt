package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.ApiImage
import com.zt.innertube.network.dto.ApiNavigationEndpoint
import com.zt.innertube.network.dto.ApiText
import com.zt.innertube.network.dto.renderer.SectionListRenderer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
internal class PlaylistContinuation private constructor(
    @ProtoNumber(80226972)
    val continuation: Continuation
) {
    constructor(id: String, page: Int) : this(
        continuation = Continuation(
            playlistIdRaw = "VL$id",
            unknown = Continuation.Unknown(
                page = page,
                unknown = "PT:CGo"
            ),
            playlistId = id
        )
    )

    @Serializable
    data class Continuation(
        @ProtoNumber(2)
        val playlistId: String,
        @ProtoNumber(3)
        val unknown: Unknown,
        @ProtoNumber(35)
        val playlistIdRaw: String,
    ) {
        @Serializable
        data class Unknown(
            @ProtoNumber(1)
            val page: Int,
            @ProtoNumber(15)
            val unknown: String
        )
    }
}

@Serializable
internal data class ApiPlaylist(
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
                val isPlayable: Boolean,
                val lengthSeconds: String,
                val lengthText: ApiText,
                val navigationEndpoint: NavigationEndpoint,
                val shortBylineText: ApiText,
                val thumbnail: ApiImage,
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
internal data class ApiPlaylistContinuation(val continuationContents: ContinuationContents) {
    @Serializable
    data class ContinuationContents(val playlistVideoListContinuation: SectionListRenderer<ApiPlaylist.SectionContent.Content>)
}