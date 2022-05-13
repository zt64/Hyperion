package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiContinuation(
    val nextContinuationData: NextContinuation? = null,
    val reloadContinuationData: ReloadContinuationData? = null
) {
    @Serializable
    data class NextContinuation(val continuation: String)

    @Serializable
    data class ReloadContinuationData(val continuation: String)
}