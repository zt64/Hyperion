package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiContinuation(
    val nextContinuationData: Continuation? = null,
    val reloadContinuationData: Continuation? = null
) {
    @Serializable
    data class Continuation(val continuation: String)
}