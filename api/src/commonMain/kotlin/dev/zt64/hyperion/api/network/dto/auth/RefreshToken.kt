package dev.zt64.hyperion.api.network.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshToken(
    @SerialName("refresh_token")
    val refreshToken: String
)