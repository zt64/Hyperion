package dev.zt64.hyperion.api.network.service

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
import dev.zt64.hyperion.api.http.KtorTransport
import dev.zt64.hyperion.api.network.body.AccessTokenBody
import dev.zt64.hyperion.api.network.body.AccountsBody
import dev.zt64.hyperion.api.network.body.BrowseBody
import dev.zt64.hyperion.api.network.body.NextBody
import dev.zt64.hyperion.api.network.body.PlayerBody
import dev.zt64.hyperion.api.network.body.RefreshTokenBody
import dev.zt64.hyperion.api.network.body.UserCodeBody
import dev.zt64.hyperion.api.network.dto.AndroidTestSuite
import dev.zt64.hyperion.api.network.dto.ApiNext
import dev.zt64.hyperion.api.network.dto.ApiNextContinuation
import dev.zt64.hyperion.api.network.dto.ApiPlayer
import dev.zt64.hyperion.api.network.dto.ApiTag
import dev.zt64.hyperion.api.network.dto.ApiTagContinuation
import dev.zt64.hyperion.api.network.dto.ApiTagParams
import dev.zt64.hyperion.api.network.dto.ClientInfo
import dev.zt64.hyperion.api.network.dto.ContinuationSerializer
import dev.zt64.hyperion.api.network.dto.InnerTubeContext
import dev.zt64.hyperion.api.network.dto.VisitorData
import dev.zt64.hyperion.api.network.dto.Web
import dev.zt64.hyperion.api.network.dto.auth.AccessToken
import dev.zt64.hyperion.api.network.dto.auth.RefreshToken
import dev.zt64.hyperion.api.network.dto.auth.UserCode
import dev.zt64.hyperion.api.network.dto.browse.ApiChannel
import dev.zt64.hyperion.api.network.dto.browse.ApiChannelContinuation
import dev.zt64.hyperion.api.network.dto.browse.ApiLibrary
import dev.zt64.hyperion.api.network.dto.browse.ApiRecommended
import dev.zt64.hyperion.api.network.dto.browse.ApiRecommendedContinuation
import dev.zt64.hyperion.api.network.dto.browse.ApiSubscriptions
import dev.zt64.hyperion.api.network.dto.browse.ApiTrending
import dev.zt64.hyperion.api.network.dto.browse.ApiTrendingContinuation
import dev.zt64.hyperion.api.network.dto.browse.ChannelParams
import dev.zt64.hyperion.api.network.dto.browse.ChannelTab
import dev.zt64.hyperion.api.network.dto.browse.IRenderer
import dev.zt64.hyperion.api.network.dto.nextModule
import dev.zt64.hyperion.api.network.dto.renderer.Renderer
import dev.zt64.hyperion.api.network.dto.renderer.UnknownRenderer
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
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.Locale
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.floor

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
    locale: Locale = Locale.getDefault(),
    val visitorData: String = generateVisitorData()
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
                    .hyperion
                    .api
                    .network
                    .dto
                    .browse
                    .Renderer::class
            ) {
                dev
                    .zt64
                    .hyperion
                    .api
                    .network
                    .dto
                    .browse
                    .UnknownRenderer
                    .serializer()
            }
        }
    }

    private val protobuf = ProtoBuf {
        encodeDefaults = true
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

    private val innerTubeContext: InnerTubeContext = InnerTubeContext(
        client = Web(
            gl = locale.country,
            hl = locale.language,
            visitorData = visitorData
        ),
        user = InnerTubeContext.User(safetyMode)
    )

    private val yt = YouTube
        .Builder(KtorTransport(engineFactory), GsonFactory.getDefaultInstance(), null)
        .setApplicationName("hyperion")
        .setYouTubeRequestInitializer(YouTubeRequestInitializer(API_KEY_TV))
        .build()

    /**
     * Perform a POST request
     */
    private suspend inline fun postRaw(
        url: String,
        crossinline request: HttpRequestBuilder.() -> Unit = {},
        crossinline body: () -> Any
    ): HttpResponse = withContext(Dispatchers.IO) {
        httpClient.post(url) {
            request()
            setBody(body())
        }
    }

    private suspend inline fun <reified T> post(
        url: String,
        crossinline request: HttpRequestBuilder.() -> Unit = {},
        crossinline body: () -> Any
    ): T = withContext(Dispatchers.IO) {
        httpClient.post(url) {
            request()
            setBody(body())
        }.body()
    }

    private suspend inline fun <reified T> browse(
        browseId: String,
        continuation: String? = null,
        params: String? = null,
        crossinline request: HttpRequestBuilder.() -> Unit = {}
    ): T = post("$API_URL/browse", request) {
        BrowseBody(
            context = innerTubeContext,
            browseId = browseId,
            continuation = continuation,
            params = params
        )
    }

    private suspend inline fun browseRaw(
        browseId: String,
        continuation: String? = null,
        params: String? = null,
        crossinline request: HttpRequestBuilder.() -> Unit = {}
    ) = postRaw("$API_URL/browse", request) {
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

    /**
     * Get the client info necessary for the OAuth flow
     *
     */
    suspend fun getClientInfo() = withContext(Dispatchers.IO) {
        val body = httpClient.get("https://www.youtube.com/tv") {
            userAgent(TV_USER_AGENT)
        }.bodyAsText()

        val (baseJs) = tvBaseJsRegex.find(body)!!.destructured
        val baseJsText = httpClient.get("https://www.youtube.com$baseJs").bodyAsText()

        val (clientId, clientSecret) = tvClientRegex.find(baseJsText)!!.destructured

        ClientInfo(clientId, clientSecret)
    }

    suspend fun getUserCode(clientId: String, deviceId: String): UserCode {
        return post("$OAUTH_URL/device/code") {
            UserCodeBody(clientId, deviceId)
        }
    }

    /**
     * Get the initial access token
     *
     * @param deviceCode
     * @param clientId
     * @param clientSecret
     * @return
     */
    suspend fun getAccessToken(deviceCode: String, clientId: String, clientSecret: String): AccessToken {
        return postRaw("$OAUTH_URL/token") {
            AccessTokenBody(
                code = deviceCode,
                clientId = clientId,
                clientSecret = clientSecret
            )
        }.body<AccessToken>()
    }

    /**
     * Get a new access token using a refresh token
     *
     * @param refreshToken
     * @param clientId
     * @param clientSecret
     * @return the new access token
     */
    internal suspend fun refreshOauth(refreshToken: String, clientId: String, clientSecret: String): RefreshToken =
        post("$OAUTH_URL/token") {
            RefreshTokenBody(refreshToken, clientId, clientSecret)
        }

    internal suspend fun getAccountsList() {
        httpClient.post("$API_URL/account/accounts_list") {
            setBody(AccountsBody(innerTubeContext))
        }
    }

    internal suspend fun getRecommendations(): ApiRecommended = browse("FEwhat_to_watch")

    internal suspend fun getRecommendations(continuation: String): ApiRecommendedContinuation {
        return browseRaw("FEwhat_to_watch", continuation).bodyAsText().let {
            json.decodeFromString(ContinuationSerializer(IRenderer.Serializer), it)
        }
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

    internal suspend fun getVideos(chart: String, pageToken: String? = null): VideoListResponse {
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

    internal suspend fun getChannel(id: String, tab: ChannelTab = ChannelTab.VIDEOS, continuation: String): ApiChannelContinuation {
        return browse(id, continuation, encodeProtobuf(ChannelParams(tab)))
    }

    internal suspend fun getChannels(ids: List<String>, part: List<String> = listOf("snippet")): ChannelListResponse {
        return yt.channels().list(part).execute {
            id = ids
        }
    }

    internal suspend fun getChannelSections(channelId: String, part: List<String> = listOf("snippet")): ChannelSectionListResponse {
        return yt.channelSections().list(part).execute {
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

    internal suspend fun getPlayer(id: String): ApiPlayer {
        return post(
            url = "$API_URL/player",
            request = {
                userAgent("com.google.android.youtube/17.36.4 (Linux; U; Android 13) gzip")
            }
        ) {
            PlayerBody(
                context = innerTubeContext.copy(
                    client = AndroidTestSuite(
                        gl = "US",
                        hl = "en",
                        visitorData = visitorData
                    )
                ),
                videoId = id
            ).also {
                println(json.encodeToString(it))
            }
        }
    }

    internal suspend fun getNext(id: String): ApiNext = post("next") {
        NextBody(
            context = innerTubeContext,
            videoId = id
        )
    }

    internal suspend fun getNext(id: String, continuation: String): ApiNextContinuation {
        return post<ApiNextContinuation>("next") {
            NextBody(
                context = innerTubeContext,
                videoId = id,
                continuation = continuation
            )
        }
    }

    internal suspend fun getComment(id: String): CommentListResponse {
        return yt.comments().list(listOf("snippet")).execute {
            this.id = listOf(id)
        }
    }

    internal suspend fun getComments(id: String, pageToken: String? = null, part: List<String> = listOf("snippet")): CommentListResponse {
        return yt.comments().list(part).execute {
            this.id = listOf(id)
            this.pageToken = pageToken
        }
    }

    internal suspend fun getSearchResults(query: String, pageToken: String? = null): SearchListResponse {
        return yt.search().list(listOf("snippet")).execute {
            q = query
            maxResults = 50
            this.pageToken = pageToken
        }
    }

    internal suspend fun getVideo(id: String, part: List<String> = listOf("snippet", "statistics", "contentDetails")): Video {
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

    internal suspend fun createWatchRecord(videoId: String) {
        httpClient.get("https://www.youtube.com/api/stats/playback") {
            parameter("ns", "yt")
            parameter("ver", 2)
            parameter("final", 1)
            parameter("docid", videoId)
            parameter("len", "0") // The video length in seconds
            parameter("st", "") // jumpFromToSec
            parameter("et", "1") // jumpFromToSecAlt
            parameter("cpn", "") // The client playback nonce
            parameter("ei", "")
            parameter("vm", "")
            parameter("of", "")
        }
    }

    internal suspend fun updateWatchTime(videoId: String) {
        httpClient.get("https://www.youtube.com/api/stats/watchtime") {
            parameter("ns", "yt")
            parameter("ver", 2)
            parameter("final", 1)
            parameter("docid", videoId)
            parameter("len", "0") // The video length in seconds
            parameter("st", "") // jumpFromToSec
            parameter("et", "1") // jumpFromToSecAlt
            parameter("cpn", "") // The client playback nonce
            parameter("ei", "")
            parameter("vm", "")
        }
    }

    internal suspend fun getChannel(id: String, pageToken: String? = null, part: List<String> = listOf("snippet")): Channel {
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

    internal suspend fun getPlaylist(id: String, part: List<String> = listOf("snippet", "contentDetails")): Playlist {
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
        // httpClient
        //     .post("$API_URL/browse") {
        //         bearerAuth(apiToken)
        //         setBody(
        //             body = BrowseBody(
        //                 context = innerTubeContext,
        //                 browseId = "FEsubscriptions"
        //             )
        //         )
        //     }.body()

        TODO()
    }

    internal suspend fun getLibrary(): ApiLibrary = browse("FElibrary") {
        bearerAuth(apiToken)
    }

    suspend fun signOut(): Unit = TODO()

    companion object {
        private const val API_URL = "https://www.youtube.com/youtubei/v1"
        private const val API_KEY_TV = "AIzaSyDCU8hByM-4DrUqRUYnGn-3llEO78bcxq8"

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

        /**
         * Generate a visitor data string
         *
         * @return the visitor data string
         */
        @OptIn(ExperimentalEncodingApi::class)
        fun generateVisitorData(): String {
            return ProtoBuf.encodeToByteArray(
                VisitorData(
                    id = buildString(11) {
                        repeat(11) { append(CHAR_POOL.random()) }
                    },
                    timestamp = floor(Clock.System.now().epochSeconds.toDouble()).toInt()
                )
            ).let {
                Base64.UrlSafe.encode(it)
                    .replace("+", "-")
                    .replace("/", "_")
            }
        }

        /**
         * Generate a client playback nonce
         *
         * @return the client playback nonce
         */
        fun generateCpn(): String {
            return buildString(16) {
                repeat(16) { append(CHAR_POOL.random()) }
            }
        }
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

private const val CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"