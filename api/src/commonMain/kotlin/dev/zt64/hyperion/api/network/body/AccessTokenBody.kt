package dev.zt64.hyperion.api.network.body

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AccessTokenBody(
    val code: String,
    @SerialName("client_id")
    val clientId: String,
    @SerialName("client_secret")
    val clientSecret: String,
    @EncodeDefault
    @SerialName("grant_type")
    val grantType: String = "http://oauth.net/grant_type/device/1.0"
)