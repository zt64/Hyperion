package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiFormat(
    val mimeType: String,
    val bitrate: Int,
    val width: Int? = null,
    val height: Int? = null,
    val itag: Int,
    val qualityLabel: String? = null,
    val url: String
)