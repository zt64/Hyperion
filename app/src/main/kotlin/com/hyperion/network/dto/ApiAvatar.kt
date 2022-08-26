package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiAvatar(
    val endpoint: Endpoint,
    val image: ApiImage
) {
    @Serializable
    data class Endpoint(val innertubeCommand: ApiNavigationEndpoint)
}