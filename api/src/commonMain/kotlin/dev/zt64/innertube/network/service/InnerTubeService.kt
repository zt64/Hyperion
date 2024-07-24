package dev.zt64.innertube.network.service

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequest
import com.google.api.services.youtube.YouTubeRequestInitializer
import com.google.api.services.youtube.model.Channel
import com.google.api.services.youtube.model.ChannelListResponse
import com.google.api.services.youtube.model.ChannelSectionListResponse
import com.google.api.services.youtube.model.CommentListResponse
import com.google.api.services.youtube.model.Playlist
import com.google.api.services.youtube.model.PlaylistItemListResponse
import com.google.api.services.youtube.model.PlaylistListResponse
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoCategoryListResponse
import com.google.api.services.youtube.model.VideoListResponse
import dev.zt64.innertube.network.body.AccessTokenBody
import dev.zt64.innertube.network.body.AccountsBody
import dev.zt64.innertube.network.body.Body
import dev.zt64.innertube.network.body.BrowseBody
import dev.zt64.innertube.network.body.NextBody
import dev.zt64.innertube.network.body.PlayerBody
import dev.zt64.innertube.network.body.RefreshTokenBody
import dev.zt64.innertube.network.body.UserCodeBody
import dev.zt64.innertube.network.dto.ApiNext
import dev.zt64.innertube.network.dto.ApiNextContinuation
import dev.zt64.innertube.network.dto.ApiPlayer
import dev.zt64.innertube.network.dto.ApiTag
import dev.zt64.innertube.network.dto.ApiTagContinuation
import dev.zt64.innertube.network.dto.ApiTagParams
import dev.zt64.innertube.network.dto.ClientInfo
import dev.zt64.innertube.network.dto.ClientName
import dev.zt64.innertube.network.dto.FormFactor
import dev.zt64.innertube.network.dto.InnerTubeContext
import dev.zt64.innertube.network.dto.Platform
import dev.zt64.innertube.network.dto.auth.AccessToken
import dev.zt64.innertube.network.dto.auth.RefreshToken
import dev.zt64.innertube.network.dto.auth.UserCode
import dev.zt64.innertube.network.dto.browse.ApiChannel
import dev.zt64.innertube.network.dto.browse.ApiChannelContinuation
import dev.zt64.innertube.network.dto.browse.ApiLibrary
import dev.zt64.innertube.network.dto.browse.ApiRecommended
import dev.zt64.innertube.network.dto.browse.ApiRecommendedContinuation
import dev.zt64.innertube.network.dto.browse.ApiSubscriptions
import dev.zt64.innertube.network.dto.browse.ApiTrending
import dev.zt64.innertube.network.dto.browse.ApiTrendingContinuation
import dev.zt64.innertube.network.dto.browse.ChannelParams
import dev.zt64.innertube.network.dto.browse.ChannelTab
import dev.zt64.innertube.network.dto.nextModule
import dev.zt64.innertube.network.dto.renderer.Renderer
import dev.zt64.innertube.network.dto.renderer.UnknownRenderer
import dev.zt64.ktor.brotli.brotli
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.Locale
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
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
            // include(searchModule)

            polymorphicDefaultDeserializer(Renderer::class) {
                UnknownRenderer.serializer()
            }
            polymorphicDefaultDeserializer(
                dev
                    .zt64
                    .innertube
                    .network
                    .dto
                    .browse
                    .Renderer::class
            ) {
                dev
                    .zt64
                    .innertube
                    .network
                    .dto
                    .browse
                    .UnknownRenderer
                    .serializer()
            }
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

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //     install(HttpCache)
        // }

        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    private val protobuf = ProtoBuf {
        encodeDefaults = true
    }

    private lateinit var innerTubeContext: InnerTubeContext
    private lateinit var visitorData: String

    val state = MutableStateFlow<State>(State.Uninitialized)

    sealed interface State {
        data object Uninitialized : State

        data object Initializing : State

        data object Initialized : State
    }

    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

    private val yt = YouTube
        .Builder(httpTransport, jsonFactory, null)
        .setApplicationName("hyperion")
        .setYouTubeRequestInitializer(YouTubeRequestInitializer(API_KEY_TV))
        .build()

    init {
        launch {
            state.emit(State.Initializing)

            val locale = Locale.getDefault()

            innerTubeContext = InnerTubeContext(
                client = InnerTubeContext.Client(
                    clientName = ClientName.WEB,
                    clientVersion = CLIENT_VERSION_WEB,
                    gl = locale.country,
                    hl = locale.language,
                    platform = Platform.DESKTOP,
                    userAgent = TV_USER_AGENT
                ),
                user = InnerTubeContext.User(safetyMode)
            )

            this@InnerTubeService.visitorData = visitorData ?: getVisitorData()
            innerTubeContext.client.visitorData = this@InnerTubeService.visitorData

            state.emit(State.Initialized)
        }
    }

    private suspend inline fun <reified T> post(
        endpoint: String,
        crossinline body: () -> Any
    ): T = httpClient.post("$API_URL/$endpoint") {
        parameter("key", API_KEY)
        setBody(body())
    }.bodyAsText().let(json::decodeFromString)

    private suspend inline fun <reified T> browse(
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

    suspend fun getUserCode(
        clientId: String,
        deviceId: String
    ): UserCode = withContext(Dispatchers.IO) {
        httpClient.post("$OAUTH_URL/device/code") {
            setBody(UserCodeBody(clientId, deviceId))
        }.body()
    }

    suspend fun getRefreshToken(
        deviceCode: String,
        clientId: String,
        clientSecret: String
    ): RefreshToken = withContext(Dispatchers.IO) {
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

    internal suspend fun getAccessToken(
        refreshToken: String,
        clientId: String,
        clientSecret: String
    ): AccessToken = withContext(Dispatchers.IO) {
        httpClient.post("$OAUTH_URL/token") {
            setBody(AccessTokenBody(refreshToken, clientId, clientSecret))
        }.body()
    }

    internal suspend fun getAccountsList() {
        httpClient.post("$API_URL/account/accounts_list") {
            setBody(AccountsBody(innerTubeContext))
        }
    }

    internal suspend fun getRecommendations(): ApiRecommended = browse("FEwhat_to_watch")

    internal suspend fun getRecommendations(continuation: String): ApiRecommendedContinuation {
        return browse("FEwhat_to_watch", continuation)
    }

    // internal suspend fun getPlaylist(id: String): ApiPlaylist = browse("VL$id")
    // internal suspend fun getPlaylist(id: String, continuation: String): ApiPlaylistContinuation {
    //     return browse(id, continuation)
    // }

    internal suspend fun getVideoCategories(regionCode: String): VideoCategoryListResponse {
        return yt.videoCategories().list(listOf("snippet")).execute {
            this.regionCode = regionCode
        }
    }

    internal suspend fun getVideos(
        chart: String,
        pageToken: String? = null
    ): VideoListResponse {
        return yt.videos().list(listOf("snippet")).execute {
            this.chart = chart
            this.regionCode = "US"
            this.pageToken = pageToken
        }
    }

    internal suspend fun getTrending(): ApiTrending = browse("FEtrending")

    internal suspend fun getTrending(continuation: String): ApiTrendingContinuation {
        return browse("FEtrending", continuation)
    }

    internal suspend fun getChannel(id: String): ApiChannel = browse(id)

    internal suspend fun getChannel(
        id: String,
        tab: ChannelTab = ChannelTab.VIDEOS,
        continuation: String
    ): ApiChannelContinuation {
        return browse(id, continuation, encodeProtobuf(ChannelParams(tab)))
    }

    internal suspend fun getChannels(ids: List<String>): ChannelListResponse {
        return yt.channels().list(listOf("snippet")).execute {
            id = ids
        }
    }

    internal suspend fun getChannelSections(channelId: String): ChannelSectionListResponse {
        return yt.channelSections().list(listOf("snippet")).execute {
            this.channelId = channelId
        }
    }

    /**
     * Get the search suggestions for a given search query
     *
     * @param search
     * @return
     */
    internal suspend fun getSearchSuggestions(search: String) = withContext(Dispatchers.IO) {
        val body = httpClient.get("https://suggestqueries-clients6.youtube.com/complete/search") {
            parameter("client", "youtube")
            parameter("ds", "yt")
            parameter("q", search)
        }.bodyAsText()

        json.parseToJsonElement(body.substringAfter("(").substringBeforeLast(")"))
    }

    internal suspend fun getPlayer(id: String): ApiPlayer = withContext(Dispatchers.IO) {
        httpClient.post("$API_URL/player") {
            userAgent("com.google.android.youtube/17.36.4 (Linux; U; Android 13) gzip")
            setBody(
                PlayerBody(
                    context = InnerTubeContext(
                        client = innerTubeContext.client.copy(
                            clientName = ClientName.ANDROID,
                            platform = Platform.MOBILE,
                            clientVersion = "17.36.4",
                            clientFormFactor = FormFactor.SMALL_FORM_FACTOR,
                            androidSdkVersion = 33,
                            userAgent = "com.google.android.youtube/17.36.4 (Linux; U; Android 13) gzip"
                        )
                    ),
                    videoId = id
                )
            )
        }.body()
    }

    internal suspend fun getNext(id: String): ApiNext = post("next") {
        NextBody(
            context = innerTubeContext,
            videoId = id
        )
    }

    internal suspend fun getNext(
        id: String,
        continuation: String
    ): ApiNextContinuation = post("next") {
        NextBody(
            context = innerTubeContext,
            videoId = id,
            continuation = continuation
        )
    }

    internal suspend fun getComment(id: String): CommentListResponse {
        return yt.comments().list(listOf("snippet")).execute {
            this.id = listOf(id)
        }
    }

    internal suspend fun getComments(
        id: String,
        pageToken: String? = null,
        part: List<String> = listOf("snippet")
    ): CommentListResponse {
        return yt.comments().list(part).execute {
            this.id = listOf(id)
            this.pageToken = pageToken
        }
    }

    internal suspend fun getSearchResults(
        query: String,
        pageToken: String? = null
    ): SearchListResponse {
        return yt.search().list(listOf("snippet")).execute {
            q = query
            maxResults = 50
            this.pageToken = pageToken
        }
    }

    internal suspend fun getVideo(
        id: String,
        part: List<String> = listOf("snippet", "statistics", "contentDetails")
    ): Video {
        return getVideos(listOf(id), null, part).items.single()
    }

    internal suspend fun getVideos(
        ids: List<String>,
        pageToken: String? = null,
        part: List<String> = listOf("snippet")
    ): VideoListResponse {
        return yt.videos().list(part).execute {
            id = ids
            this.pageToken = pageToken
        }
    }

    internal suspend fun getChannel(
        id: String,
        pageToken: String? = null,
        part: List<String> = listOf("snippet")
    ): Channel {
        return getChannels(listOf(id), pageToken, part).items.single()
    }

    internal suspend fun getChannels(
        ids: List<String>,
        pageToken: String? = null,
        part: List<String> = listOf("snippet")
    ): ChannelListResponse {
        return yt.channels().list(part).execute {
            id = ids
            this.pageToken = pageToken
        }
    }

    internal suspend fun getPlaylist(
        id: String,
        part: List<String> = listOf("snippet", "contentDetails")
    ): Playlist {
        return getPlaylists(listOf(id), null, part).items.single()
    }

    internal suspend fun getPlaylists(
        ids: List<String>,
        pageToken: String? = null,
        part: List<String> = listOf("snippet", "contentDetails")
    ): PlaylistListResponse {
        return yt.playlists().list(part).execute {
            id = ids
            this.pageToken = pageToken
        }
    }

    internal suspend fun getPlaylistItems(
        id: String,
        pageToken: String? = null,
        part: List<String> = listOf("snippet", "contentDetails")
    ): PlaylistItemListResponse {
        return yt.playlistItems().list(part).execute {
            this.id = listOf(id)
            this.pageToken = pageToken
        }
    }

    // internal suspend fun getSearchResults(
    //     query: String,
    //     continuation: String
    // ): ApiSearchContinuation = post("search") {
    //     SearchBody(
    //         context = innerTubeContext,
    //         query = query,
    //         continuation = continuation
    //     )
    // }

    internal suspend fun getTag(tag: String): ApiTag = browse(
        browseId = "FEhashtag",
        params = encodeProtobuf(
            ApiTagParams(ApiTagParams.Context(tag.removePrefix("#").lowercase()))
        )
    )

    internal suspend fun getTagItems(tag: String) {
    }

    internal suspend fun getTagContinuation(continuation: String): ApiTagContinuation {
        return browse("FEhashtag", continuation)
    }

    private val apiToken: String = ""

    internal suspend fun getSubscriptions(): ApiSubscriptions = withContext(Dispatchers.IO) {
        yt.subscriptions().list(listOf("snippet")).execute {
            mine = true
        }
        httpClient
            .post("$API_URL/browse") {
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
        httpClient
            .post("$API_URL/browse") {
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
    private suspend fun getVisitorData(): String {
        return withContext(Dispatchers.IO) {
            post<VisitorId>("visitor_id") { Body(innerTubeContext) }.responseContext.visitorData
        }
    }

    suspend fun signOut(): Unit = TODO()

    companion object {
        private const val API_URL = "https://www.youtube.com/youtubei/v1"
        private const val API_KEY = "AIzaSyCtkvNIR1HCEwzsqK6JuE6KqpyjusIRI30"
        private const val API_KEY_TV = "AIzaSyDCU8hByM-4DrUqRUYnGn-3llEO78bcxq8"

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

        private val tvBaseJsRegex = Regex("""<script id="base-js" src="(.*?)"""")
        private val tvClientRegex =
            Regex("""clientId:"([-\w]+\.apps\.googleusercontent\.com)",\w+:"(\w+)"""")

        /**
         * Get the video thumbnail url for a given video id
         *
         * @param id the video id
         */
        fun getVideoThumbnail(id: String) = "https://i.ytimg.com/vi/$id/hqdefault.jpg"
    }

    @OptIn(ExperimentalContracts::class)
    private suspend fun <T : YouTubeRequest<R>, R : Any> T.execute(block: T.() -> Unit): R {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        block()
        return withContext(Dispatchers.IO) { execute() }
    }
}

@Serializable
private data class VisitorId(val responseContext: ResponseContext) {
    @Serializable
    data class ResponseContext(val visitorData: String)
}