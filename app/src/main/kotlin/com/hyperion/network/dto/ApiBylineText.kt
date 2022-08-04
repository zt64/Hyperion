package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiBylineText(val runs: List<Run>) {
    @Serializable
    data class Run(
        val navigationEndpoint: ApiNavigationEndpoint,
        val text: String
    )
}