package dev.zt64.hyperion.api.model

import com.google.api.services.youtube.model.ThumbnailDetails

class Thumbnails private constructor(
    val default: Thumbnail,
    val medium: Thumbnail,
    val high: Thumbnail
) {
    constructor(thumbnailDetails: ThumbnailDetails) : this(
        default = Thumbnail(thumbnailDetails.default),
        medium = Thumbnail(thumbnailDetails.medium),
        high = Thumbnail(thumbnailDetails.high)
    )
}

data class Thumbnail(
    val url: String,
    val width: Long,
    val height: Long
) {
    constructor(thumbnail: com.google.api.services.youtube.model.Thumbnail) : this(
        url = thumbnail.url,
        width = thumbnail.width,
        height = thumbnail.height
    )
}