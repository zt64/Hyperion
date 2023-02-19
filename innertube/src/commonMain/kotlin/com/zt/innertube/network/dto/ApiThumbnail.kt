package com.zt.innertube.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiThumbnailTimestamp(
    val image: ApiImage,
    val timestampText: String? = null
)