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
        val clientName: ClientName,
        val clientVersion: String,
        val gl: String? = null,
        val hl: String? = null,
        val userAgent: String,
        val platform: Platform? = null,
        var visitorData: String? = null,
        val clientFormFactor: FormFactor? = FormFactor.UNKNOWN_FORM_FACTOR,
        val androidSdkVersion: Int? = null
    )

    @Serializable
    data class Request(val useSsl: Boolean = false)

    @Serializable
    data class User(val lockedSafetyMode: Boolean = false)
}

sealed interface Client {
    val clientName: ClientName
    val clientVersion: String
    val gl: String?
        get() = null
    val hl: String?
        get() = null
    val userAgent: String
    val platform: Platform?
        get() = null
    var visitorData: String?
    val clientFormFactor: FormFactor?
        get() = FormFactor.UNKNOWN_FORM_FACTOR

    @Serializable
    data class Android(
        override val hl: String?,
        override val gl: String?,
        override var visitorData: String?,
        override val clientName: ClientName = ClientName.ANDROID,
        override val clientVersion: String = "17.36.4",
        override val platform: Platform = Platform.MOBILE,
        override val clientFormFactor: FormFactor = FormFactor.SMALL_FORM_FACTOR,
        override val userAgent: String = "com.google.android.youtube/17.36.4 (Linux; U; Android 13) gzip",
        val androidSdkVersion: Int = 33
    ) : Client
}

enum class ClientName {
    ANDROID,
    WEB,
    MWEB
}

enum class Platform {
    MOBILE,
    DESKTOP
}

enum class FormFactor {
    UNKNOWN_FORM_FACTOR,
    SMALL_FORM_FACTOR
}