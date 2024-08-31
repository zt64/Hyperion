package dev.zt64.hyperion.api.network.body

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class used for requesting a user code. The oauth entry point for the device code flow.
 *
 * @property clientId
 * @property deviceId
 * @property modelName
 * @property scope
 */
@Serializable
internal data class UserCodeBody(
    @SerialName("client_id")
    val clientId: String,
    @SerialName("device_id")
    val deviceId: String,
    @EncodeDefault
    @SerialName("model_name")
    val modelName: String = "ytlr::",
    @EncodeDefault
    val scope: String = "http://gdata.youtube.com https://www.googleapis.com/auth/youtube-paid-content"
)