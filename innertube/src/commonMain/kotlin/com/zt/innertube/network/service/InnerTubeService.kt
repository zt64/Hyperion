package com.zt.innertube.network.service

import com.zt.innertube.network.body.*
import com.zt.innertube.network.dto.*
import com.zt.innertube.network.dto.browse.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.protobuf.ProtoBuf
import java.net.URLEncoder
import java.util.Locale

class InnerTubeService(
    private val httpClient: HttpClient,
    private val json: Json
) {
    private var innerTubeContext: ApiContext

    init {
        runBlocking(Dispatchers.IO) {
            val body = httpClient.get(YOUTUBE_URL).bodyAsText()
            val (ytCfg) = """ytcfg\.set\((.*?)\);""".toRegex()
                .findAll(body)
                .elementAt(1)
                .destructured

            val (context) = json.decodeFromString<InnerTubeData>(ytCfg)
            val locale = Locale.getDefault()

            innerTubeContext = ApiContext(
                client = ApiContext.Client(
                    clientName = CLIENT_NAME,
                    clientVersion = CLIENT_VERSION,
                    gl = locale.country,
                    hl = locale.language,
                    platform = PLATFORM,
                    userAgent = context.client.userAgent,
                    visitorData = context.client.visitorData,
                    clientFormFactor = FORM_FACTOR
                ),
                request = context.request,
                user = context.user
            )
        }
    }

    private suspend fun post(endpoint: String, body: Body) = withContext(Dispatchers.IO) {
        httpClient.post("$API_URL/$endpoint") {
            parameter("key", API_KEY)
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    private suspend inline fun <reified T> getBrowse(
        browseId: String,
        continuation: String? = null,
        params: String? = null
    ): T = post(
        endpoint = "browse",
        body = BrowseBody(
            context = innerTubeContext,
            browseId = browseId,
            continuation = continuation,
            params = params
        )
    ).body()

    internal suspend fun getRecommendations(): ApiRecommended = getBrowse("FEwhat_to_watch")
    internal suspend fun getRecommendations(continuation: String): ApiRecommendedContinuation = getBrowse("FEwhat_to_watch", continuation)

    internal suspend fun getPlaylist(id: String): ApiPlaylist = getBrowse(id)
    internal suspend fun getPlaylist(id: String, continuation: String): ApiPlaylistContinuation = getBrowse(id, continuation)

    internal suspend fun getTrending(): ApiTrending = getBrowse("FEtrending")
    internal suspend fun getTrending(continuation: String): ApiTrendingContinuation = getBrowse("FEtrending", continuation)

    internal suspend fun getChannel(id: String, params: String? = null): ApiChannel = getBrowse(id, params = params)

    internal suspend fun getSearchSuggestions(search: String): JsonElement = withContext(Dispatchers.IO) {
        val body = httpClient.get("https://suggestqueries-clients6.youtube.com/complete/search") {
            parameter("client", "youtube")
            parameter("ds", "yt")
            parameter("q", search)
        }.bodyAsText()

        json.parseToJsonElement(body.substringAfter("(").substringBeforeLast(")"))
    }

    internal suspend fun getPlayer(id: String): ApiPlayer = post(
        endpoint = "player",
        body = PlayerBody(
            context = innerTubeContext,
            videoId = id
        )
    ).body()

    internal suspend fun getNext(id: String): ApiNext = post(
        endpoint = "next",
        body = NextBody(
            context = innerTubeContext,
            videoId = id
        )
    ).body()

    internal suspend fun getNext(id: String, continuation: String): ApiNextContinuation = post(
        endpoint = "next",
        body = NextBody(
            context = innerTubeContext,
            videoId = id,
            continuation = continuation
        )
    ).body()

    internal suspend fun getSearchResults(query: String): ApiSearch = post(
        endpoint = "search",
        body = SearchBody(
            context = innerTubeContext,
            query = query
        )
    ).body()

    internal suspend fun getSearchResults(query: String, continuation: String): ApiSearchContinuation = post(
        endpoint = "search",
        body = SearchBody(
            context = innerTubeContext,
            query = query,
            continuation = continuation
        )
    ).body()

    internal suspend fun getTag(tag: String): ApiTag = getBrowse(
        browseId = "FEhashtag",
        params = withContext(Dispatchers.IO) {
            URLEncoder.encode(
                /* s = */ ProtoBuf.encodeToByteArray(
                    ApiTagParams(ApiTagParams.Context(tag.removePrefix("#").lowercase()))
                ).encodeBase64(),
                /* enc = */ Charsets.UTF_8.name()
            )
        }
    )

    internal suspend fun getTagContinuation(continuation: String): ApiTagContinuation = getBrowse("FEhashtag", continuation)

    internal suspend fun getSubscriptions(): ApiSubscriptions = TODO()

    companion object {
        private const val YOUTUBE_URL = "https://www.youtube.com"
        private const val API_URL = "https://www.youtube.com/youtubei/v1"
        private const val API_KEY = "AIzaSyCtkvNIR1HCEwzsqK6JuE6KqpyjusIRI30"

        // TODO: Use web client for fetching data
        private const val CLIENT_NAME = "ANDROID"
        private const val CLIENT_VERSION = "17.11.37"
        private const val PLATFORM = "MOBILE"
        private const val FORM_FACTOR = "SMALL_FORM_FACTOR"

        private const val OAUTH_URL = "https://www.youtube.com/o/oauth2"
        private const val TV_USER_AGENT = "Mozilla/5.0 (ChromiumStylePlatform) Cobalt/Version"
        private val tvBaseJsRegex = """<script id="base-js" src="(.*?)" nonce=".*?"></script>""".toRegex()
        private val tvClientRegex = """clientId:"([-\w]+\.apps\.googleusercontent\.com)",\w+:"(\w+)"""".toRegex()

        fun getVideoThumbnail(id: String) = "https://i.ytimg.com/vi/$id/hqdefault.jpg"
    }
}

@Serializable
private data class InnerTubeData(
    @SerialName("INNERTUBE_CONTEXT")
    val innerTubeContext: ApiContext
)