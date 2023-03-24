package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.ApiImage
import com.zt.innertube.network.dto.ApiText
import com.zt.innertube.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiHistory(
    override val contents: Contents<SectionContent>
) : ApiBrowse() {
    @Serializable
    data class SectionContent(val itemSectionRenderer: ItemSectionRenderer<Content>? = null) {
        @Serializable
        data class Content(val compactVideoRenderer: CompactVideoRenderer) {
            @Serializable
            data class CompactVideoRenderer(
                val channelThumbnail: ApiImage,
                val lengthText: ApiText,
                val longBylineText: ApiImage,
                val shortBylineText: ApiImage,
                val shortViewCountText: ApiText,
                val thumbnail: ApiImage,
                val thumbnailOverlays: List<ThumbnailOverlay>,
                val title: ApiText,
                val videoId: String,
                val viewCountText: ApiImage
            ) {
                @Serializable
                data class ThumbnailOverlay(
                    val thumbnailOverlayResumePlaybackRenderer: ThumbnailOverlayResumePlaybackRenderer,
                    val thumbnailOverlayTimeStatusRenderer: ThumbnailOverlayTimeStatusRenderer
                ) {
                    @Serializable
                    data class ThumbnailOverlayResumePlaybackRenderer(val percentDurationWatched: Int)

                    @Serializable
                    data class ThumbnailOverlayTimeStatusRenderer(val text: ApiText)
                }
            }
        }
    }
}

@Serializable
internal data class ApiHistoryContinuation(
    override val onResponseReceivedActions: List<ContinuationContents<ApiTrending.SectionContent>>
) : ApiBrowseContinuation()