package dev.zt64.innertube.network.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AccessToken(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    val scope: String,
    @SerialName("token_type")
    val tokenType: String
)

sealed interface AuthEvent {
    /**
     * The code has expired
     */
    data object Expired : AuthEvent

    /**
     * The user has not yet authenticated
     */
    data object Pending : AuthEvent

    /**
     * Device code already exchanged
     */
    data object Invalid : AuthEvent

    /**
     * The user has authenticated
     */
    @Serializable
    data class Authenticated(
        @SerialName("access_token")
        val accessToken: String,
        @SerialName("expires_in")
        val expiresIn: Int,
        @SerialName("id_token")
        val idToken: String,
        @SerialName("refresh_token")
        val refreshToken: String,
        val scope: String,
        @SerialName("token_type")
        val tokenType: String
    ) : AuthEvent
}