package com.zt.innertube.network.service

import android.os.Build
import com.zt.innertube.network.body.*
import com.zt.innertube.network.dto.*
import com.zt.innertube.network.dto.auth.AccessToken
import com.zt.innertube.network.dto.auth.RefreshToken
import com.zt.innertube.network.dto.auth.UserCode
import com.zt.innertube.network.dto.browse.*
import com.zt.ktor.brotli.brotli
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.Locale
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * TODO
 *
 * @constructor
 * TODO
 *
 * @param engineFactory
 * @param cookiesStorage
 * @param safetyMode Whether to use safety mode or not
 * @param visitorData Visitor data
 */
class InnerTubeService(
    engineFactory: HttpClientEngineFactory<*>,
    cookiesStorage: CookiesStorage = AcceptAllCookiesStorage(),
    safetyMode: Boolean = false,
    visitorData: String? = null
) : CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {
    private val json = Json {
        ignoreUnknownKeys = true

        serializersModule = SerializersModule {
            include(nextModule)
            include(searchModule)
        }
    }

    private val httpClient = HttpClient(engineFactory) {
        // TODO: Use a custom user agent
        BrowserUserAgent()

        install(ContentNegotiation) {
            json(json)
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
            exponentialDelay()
        }

        install(ContentEncoding) {
            deflate()
            gzip()
            brotli()
        }

        install(HttpCookies) {
            storage = cookiesStorage
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            install(HttpCache)
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    private val protobuf = ProtoBuf {
        encodeDefaults = true
    }

    private lateinit var innerTubeContext: InnerTubeContext
    val state = MutableStateFlow<State>(State.Uninitialized)

    sealed interface State {
        object Uninitialized : State
        object Initializing : State
        object Initialized : State
    }

    init {
        launch {
            state.emit(State.Initializing)

            val locale = Locale.getDefault()

            innerTubeContext = InnerTubeContext(
                client = InnerTubeContext.Client(
                    clientName = CLIENT_NAME_WEB,
                    clientVersion = CLIENT_VERSION_WEB,
                    gl = locale.country,
                    hl = locale.language,
                    platform = PLATFORM_WEB,
                    userAgent = TV_USER_AGENT
                ),
                user = InnerTubeContext.User(safetyMode)
            )

            innerTubeContext.client.visitorData = getVisitorData()

            state.emit(State.Initialized)
        }
    }

    private suspend inline fun <reified T> post(endpoint: String, crossinline body: () -> Any) = withContext(Dispatchers.IO) {
        httpClient.post("$API_URL/$endpoint") {
            parameter("key", API_KEY)
            setBody(body())
        }.body<T>()
    }

    private suspend inline fun <reified T> getBrowse(
        browseId: String,
        continuation: String? = null,
        params: String? = null
    ): T = post("browse") {
        BrowseBody(
            context = innerTubeContext,
            browseId = browseId,
            continuation = continuation,
            params = params
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    private suspend inline fun <reified T> encodeProtobuf(value: T) = withContext(Dispatchers.IO) {
        Base64.UrlSafe.encode(protobuf.encodeToByteArray(value))
    }

    suspend fun getClientInfo() = withContext(Dispatchers.IO) {
        val body = httpClient.get("https://www.youtube.com/tv") {
            userAgent(TV_USER_AGENT)
        }.bodyAsText()

        val (baseJs) = tvBaseJsRegex.find(body)!!.destructured
        val baseJsText = httpClient.get("https://www.youtube.com$baseJs").bodyAsText()

        val (clientId, clientSecret) = tvClientRegex.find(baseJsText)!!.destructured

        ClientInfo(clientId, clientSecret)
    }

    suspend fun getUserCode(clientId: String, deviceId: String): UserCode = withContext(Dispatchers.IO) {
        httpClient.post("$OAUTH_URL/device/code") {
            setBody(UserCodeBody(clientId, deviceId))
        }.body()
    }

    suspend fun getRefreshToken(deviceCode: String, clientId: String, clientSecret: String): RefreshToken = withContext(Dispatchers.IO) {
        httpClient.post("$OAUTH_URL/token") {
            setBody(
                body = RefreshTokenBody(
                    code = deviceCode,
                    clientId = clientId,
                    clientSecret = clientSecret
                )
            )
        }.body()
    }

    internal suspend fun getAccessToken(refreshToken: String, clientId: String, clientSecret: String): AccessToken = withContext(Dispatchers.IO) {
        httpClient.post("$OAUTH_URL/token") {
            setBody(AccessTokenBody(refreshToken, clientId, clientSecret))
        }.body()
    }

    internal suspend fun getAccountsList() {
        httpClient.post("$API_URL/account/accounts_list") {
            setBody(AccountsBody(innerTubeContext))
        }
    }

    internal suspend fun getRecommendations(): ApiRecommended = getBrowse("FEwhat_to_watch")
    internal suspend fun getRecommendations(continuation: String): ApiRecommendedContinuation = getBrowse("FEwhat_to_watch", continuation)

    internal suspend fun getPlaylist(id: String): ApiPlaylist = getBrowse("VL$id")
    internal suspend fun getPlaylist(id: String, continuation: String): ApiPlaylistContinuation = getBrowse(id, continuation)

    internal suspend fun getTrending(): ApiTrending = getBrowse("FEtrending")
    internal suspend fun getTrending(continuation: String): ApiTrendingContinuation = getBrowse("FEtrending", continuation)

    internal suspend fun getChannel(id: String): ApiChannel = getBrowse(id)
    internal suspend fun getChannel(id: String, tab: ChannelTab = ChannelTab.VIDEOS): ApiChannel = getBrowse(id, params = encodeProtobuf(ChannelParams(tab)))
    internal suspend fun getChannel(id: String, tab: ChannelTab = ChannelTab.VIDEOS, continuation: String): ApiChannelContinuation =
        getBrowse(id, continuation, encodeProtobuf(ChannelParams(tab)))

    /**
     * Get the search suggestions for a given search query
     *
     * @param search
     * @return
     */
    internal suspend fun getSearchSuggestions(search: String): JsonElement = withContext(Dispatchers.IO) {
        val body = httpClient.get("https://suggestqueries-clients6.youtube.com/complete/search") {
            parameter("client", "youtube")
            parameter("ds", "yt")
            parameter("q", search)
        }.bodyAsText()

        json.parseToJsonElement(body.substringAfter("(").substringBeforeLast(")"))
    }

    internal suspend fun getPlayer(id: String): ApiPlayer = post("player") {
        PlayerBody(
            context = innerTubeContext.copy(
                client = innerTubeContext.client.copy(
                    clientName = "MWEB",
                    platform = "MOBILE",
                    clientVersion = "2.20230502.01.00",
                    clientFormFactor = "SMALL_FORM_FACTOR"
                )
            ),
            videoId = id
        )
    }

    internal suspend fun getNext(id: String): ApiNext = post("next") {
        NextBody(
            context = innerTubeContext,
            videoId = id
        )
    }

    internal suspend fun getNext(id: String, continuation: String): ApiNextContinuation = post("next") {
        NextBody(
            context = innerTubeContext,
            videoId = id,
            continuation = continuation
        )
    }

    internal suspend fun getComments(id: String, page: Int = 1) = getNext(
        id = id,
        continuation = encodeProtobuf(CommentParams(id, page))
    )

    internal suspend fun getSearchResults(query: String): ApiSearch = post("search") {
        SearchBody(
            context = innerTubeContext,
            query = query
        )
    }

    internal suspend fun getSearchResults(query: String, continuation: String): ApiSearchContinuation = post("search") {
        SearchBody(
            context = innerTubeContext,
            query = query,
            continuation = continuation
        )
    }

    internal suspend fun getTag(tag: String): ApiTag = getBrowse(
        browseId = "FEhashtag",
        params = encodeProtobuf(ApiTagParams(ApiTagParams.Context(tag.removePrefix("#").lowercase())))
    )

    internal suspend fun getTagContinuation(continuation: String): ApiTagContinuation = getBrowse("FEhashtag", continuation)

    private val apiToken: String = ""

    internal suspend fun getSubscriptions(): ApiSubscriptions = withContext(Dispatchers.IO) {
        httpClient.post("$API_URL/browse") {
            bearerAuth(apiToken)
            setBody(
                body = BrowseBody(
                    context = innerTubeContext,
                    browseId = "FEsubscriptions"
                )
            )
        }.body()
    }

    internal suspend fun getLibrary(): ApiLibrary = withContext(Dispatchers.IO) {
        httpClient.post("$API_URL/browse") {
            bearerAuth(apiToken)
            setBody(
                body = BrowseBody(
                    context = innerTubeContext,
                    browseId = "FElibrary"
                )
            )
        }.body()
    }

    /**
     * Get the visitor data, necessary for recommendations and other requests
     *
     * @return visitor data
     */
    private suspend fun getVisitorData(): String = withContext(Dispatchers.IO) {
        post<VisitorId>("visitor_id") { Body(innerTubeContext) }.responseContext.visitorData
    }

    companion object {
        private const val API_URL = "https://www.youtube.com/youtubei/v1"
        private const val API_KEY = "AIzaSyCtkvNIR1HCEwzsqK6JuE6KqpyjusIRI30"

        private const val CLIENT_NAME_WEB = "WEB"
        private const val CLIENT_VERSION_WEB = "2.20230221.01.00"
        private const val PLATFORM_WEB = "DESKTOP"
        private const val FORM_FACTOR_WEB = "UNKNOWN_FORM_FACTOR"

        private const val CLIENT_NAME_ANDROID = "ANDROID"
        private const val CLIENT_VERSION_ANDROID = "17.11.37"
        private const val PLATFORM_ANDROID = "MOBILE"
        private const val FORM_FACTOR_ANDROID = "SMALL_FORM_FACTOR"

        private const val OAUTH_URL = "https://www.youtube.com/o/oauth2"
        private const val TV_USER_AGENT = "Mozilla/5.0 (ChromiumStylePlatform) Cobalt/Version"

        private val tvBaseJsRegex = """<script id="base-js" src="(.*?)" nonce=".*?"></script>""".toRegex()
        private val tvClientRegex = """clientId:"([-\w]+\.apps\.googleusercontent\.com)",\w+:"(\w+)"""".toRegex()

        /**
         * Get the video thumbnail url for a given video id
         *
         * @param id the video id
         */
        fun getVideoThumbnail(id: String) = "https://i.ytimg.com/vi/$id/hqdefault.jpg"
    }
}

@Serializable
private data class VisitorId(val responseContext: ResponseContext) {
    @Serializable
    data class ResponseContext(val visitorData: String)
}
