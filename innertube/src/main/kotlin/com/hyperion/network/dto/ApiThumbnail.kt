package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiThumbnail(val thumbnails: List<Thumbnail>) {
    @Serializable
    data class Thumbnail(
        val width: Int,
        val height: Int,
        val url: String
    )
}

@Serializable
data class ApiThumbnailTimestamp(
    val image: ApiImage,
    val timestampText: String? = null
)