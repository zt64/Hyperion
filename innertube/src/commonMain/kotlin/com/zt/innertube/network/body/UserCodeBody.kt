package com.zt.innertube.network.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserCodeBody(
    @SerialName("client_id")
    val clientId: String,
    @SerialName("device_id")
    val deviceId: String
) {
    @SerialName("model_name")
    val modelName = "ytlr::"
    val scope = "http://gdata.youtube.com https://www.googleapis.com/auth/youtube-paid-content"
}