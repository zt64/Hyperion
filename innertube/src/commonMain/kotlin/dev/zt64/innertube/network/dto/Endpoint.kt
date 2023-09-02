package dev.zt64.innertube.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiWatchCommand(val watchEndpoint: ApiWatchEndpoint? = null)

@Serializable
internal data class ApiWatchEndpoint(val videoId: String)

@Serializable
internal data class ApiBrowseEndpoint(val browseId: String)

@Serializable
internal data class ApiNavigationEndpoint(val browseEndpoint: ApiBrowseEndpoint)