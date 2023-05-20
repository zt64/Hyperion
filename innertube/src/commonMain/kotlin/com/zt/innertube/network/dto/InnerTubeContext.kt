package com.zt.innertube.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class InnerTubeContext(
    val client: Client,
    val request: Request? = Request(),
    val user: User? = User()
) {
    @Serializable
    data class Client(
        val clientName: String,
        val clientVersion: String,
        val gl: String? = null,
        val hl: String? = null,
        val userAgent: String,
        val platform: String? = null,
        var visitorData: String? = null,
        val clientFormFactor: String? = "UNKNOWN_FORM_FACTOR"
    )

    @Serializable
    data class Request(val useSsl: Boolean = false)

    @Serializable
    data class User(val lockedSafetyMode: Boolean = false)
}