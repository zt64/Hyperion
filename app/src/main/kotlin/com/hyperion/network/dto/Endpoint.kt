package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiWatchEndpoint(val watchEndpoint: WatchEndpoint) {
    @Serializable
    data class WatchEndpoint(val videoId: String)
}

@Serializable
data class ApiBrowseEndpoint(
    val browseId: String
)