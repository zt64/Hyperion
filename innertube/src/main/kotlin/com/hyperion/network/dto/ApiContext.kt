package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiContext(
    val client: Client,
    val request: Request,
    val user: User
) {
    @Serializable
    data class Client(
        val clientName: String,
        val clientVersion: String,
        val gl: String,
        val hl: String,
        val platform: String,
        val userAgent: String,
        val visitorData: String,
        val clientFormFactor: String
    )

    @Serializable
    data class Request(val useSsl: Boolean)

    @Serializable
    data class User(val lockedSafetyMode: Boolean)
}