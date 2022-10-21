package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiImage(val sources: List<Source>) {
    @Serializable
    data class Source(val url: String)
}

@Serializable
data class ImageContainer(val image: ApiImage)