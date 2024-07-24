package dev.zt64.innertube.network.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RefreshTokenBody(
    val code: String,
    @SerialName("client_id")
    val clientId: String,
    @SerialName("client_secret")
    val clientSecret: String
) {
    @SerialName("grant_type")
    val grantType = "http://oauth.net/grant_type/device/1.0"
}