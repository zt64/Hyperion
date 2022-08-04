package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiThumbnailOverlay(val thumbnailOverlayTimeStatusRenderer: ThumbnailOverlayTimeStatusRenderer) {
    @Serializable
    data class ThumbnailOverlayTimeStatusRenderer(val text: ApiText)
}