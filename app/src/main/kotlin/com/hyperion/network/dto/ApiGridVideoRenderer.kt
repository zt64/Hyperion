package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiGridVideoRenderer(
    val channelThumbnail: ApiThumbnail,
    val lengthText: ApiText,
    val publishedTimeText: ApiText,
    val shortBylineText: ByLineText,
    val shortViewCountText: ApiText,
    val thumbnail: ApiThumbnail,
    val thumbnailOverlays: List<ThumbnailOverlay>,
    val title: ApiText,
    val videoId: String,
    val viewCountText: ApiText
) {
    @Serializable
    data class ByLineText(val runs: List<Run>) {
        @Serializable
        data class Run(
            val navigationEndpoint: NavigationEndpoint,
            val text: String
        )
    }

    @Serializable
    data class NavigationEndpoint(val browseEndpoint: BrowseEndpoint) {
        @Serializable
        data class BrowseEndpoint(
            val browseId: String,
            val canonicalBaseUrl: String
        )
    }

    @Serializable
    data class ThumbnailOverlay(val thumbnailOverlayTimeStatusRenderer: ThumbnailOverlayTimeStatusRenderer) {
        @Serializable
        data class ThumbnailOverlayTimeStatusRenderer(val text: ApiText)
    }
}