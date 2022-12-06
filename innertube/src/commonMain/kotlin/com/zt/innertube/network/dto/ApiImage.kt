package com.zt.innertube.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiImage(val sources: List<Source>) {
    @Serializable
    data class Source(val url: String)
}

@Serializable
internal data class ImageContainer(val image: ApiImage)