package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiWatchCommand(val watchEndpoint: ApiWatchEndpoint)

@Serializable
data class ApiWatchEndpoint(val videoId: String)

@Serializable
data class ApiBrowseEndpoint(val browseId: String)

@Serializable
data class ApiNavigationEndpoint(val browseEndpoint: ApiBrowseEndpoint)