package dev.zt64.innertube.network.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property deviceCode The UUID code for the device
 * @property userCode The code the user must enter on the device
 * @property expiresIn The number of milliseconds until the code expires
 * @property interval The number of seconds to wait between polling requests
 */
@Serializable
data class UserCode(
    @SerialName("device_code")
    val deviceCode: String,
    @SerialName("user_code")
    val userCode: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    val interval: Int
)