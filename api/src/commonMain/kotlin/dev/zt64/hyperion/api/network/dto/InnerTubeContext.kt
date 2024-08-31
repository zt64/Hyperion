package dev.zt64.hyperion.api.network.dto

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
internal data class InnerTubeContext(
    val client: Client,
    @EncodeDefault
    val request: Request? = Request(),
    @EncodeDefault
    val user: User? = User()
) {
    @Serializable
    data class Request(val useSsl: Boolean = false)

    @Serializable
    data class User(val lockedSafetyMode: Boolean = false)
}

@JsonClassDiscriminator("clientName")
@Serializable
sealed interface Client {
    @EncodeDefault
    val clientVersion: String

    @EncodeDefault
    val userAgent: String

    @EncodeDefault
    val platform: Platform
    val gl: String? get() = null
    val hl: String? get() = null
    val visitorData: String? get() = null
    val clientFormFactor: FormFactor? get() = null
}

@SerialName("ANDROID")
@Serializable
data class Android(
    override val hl: String?,
    override val gl: String?,
    override var visitorData: String?,
    // override val clientName: ClientName = ClientName.ANDROID,
    @EncodeDefault
    override val clientVersion: String = "17.36.4",
    @EncodeDefault
    override val platform: Platform = Platform.MOBILE,
    @EncodeDefault
    override val clientFormFactor: FormFactor = FormFactor.SMALL_FORM_FACTOR,
    @EncodeDefault
    override val userAgent: String = ANDROID_USER_AGENT,
    val androidSdkVersion: Int = 33
) : Client

@SerialName("ANDROID_TESTSUITE")
@Serializable
data class AndroidTestSuite(
    override val hl: String?,
    override val gl: String?,
    override var visitorData: String?,
    // override val clientName: ClientName = ClientName.ANDROID_TEST_SUITE,
    override val clientVersion: String = "1.9",
    override val platform: Platform = Platform.MOBILE,
    override val userAgent: String = "com.google.android.youtube/1.9 (Linux; U; Android 12; US) gzip",
    val androidSdkVersion: Int = 31,
    val osName: String = "Android",
    val osVersion: String = "12"
) : Client

@SerialName("WEB")
@Serializable
data class Web(
    override val hl: String?,
    override val gl: String?,
    override var visitorData: String?,
    // override val clientName: ClientName = ClientName.WEB,
    @EncodeDefault
    override val clientVersion: String = "2.20210922.00.00",
    @EncodeDefault
    override val platform: Platform = Platform.DESKTOP,
    @EncodeDefault
    override val userAgent: String = WEB_USER_AGENT
) : Client

const val ANDROID_USER_AGENT = "com.google.android.youtube/17.36.4 (Linux; U; Android 13) gzip"
const val WEB_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36"

enum class ClientName {
    ANDROID,

    @SerialName("ANDROID_TESTSUITE")
    ANDROID_TEST_SUITE,
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