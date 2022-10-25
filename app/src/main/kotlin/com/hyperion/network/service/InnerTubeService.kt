package com.hyperion.network.service

import com.hyperion.network.body.*
import com.hyperion.network.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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

class InnerTubeService(
    private val httpClient: HttpClient,
    private val json: Json
) {
    private var innerTubeApiKey: String
    private var innerTubeContext: ApiContext

    init {
        runBlocking {
            val body = httpClient.get(YOUTUBE_URL).bodyAsText()

            val (obj) = """ytcfg\.set\((.*?)\);""".toRegex()
                .findAll(body)
                .elementAt(1)
                .destructured

            val data: InnerTubeData = json.decodeFromString(obj)

            innerTubeApiKey = data.innerTubeApiKey
            innerTubeContext = data.innerTubeContext.copy(
                client = data.innerTubeContext.client.copy(
                    clientName = CLIENT_NAME,
                    clientVersion = CLIENT_VERSION,
                    platform = PLATFORM,
                    clientFormFactor = FORM_FACTOR
                )
            )
        }
    }

    private suspend fun post(endpoint: String, body: Body) = withContext(Dispatchers.IO) {
        httpClient.post("$API_URL/$endpoint") {
            parameter("key", innerTubeApiKey)
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

    suspend fun getRecommendations(): ApiRecommended = getBrowse("FEwhat_to_watch")
    suspend fun getRecommendations(continuation: String): ApiRecommendedContinuation = getBrowse("FEwhat_to_watch", continuation)

    suspend fun getPlaylist(id: String): ApiPlaylist = getBrowse(id)
    suspend fun getPlaylist(id: String, continuation: String): ApiPlaylistContinuation = getBrowse(id, continuation)

    suspend fun getSubscriptions(): ApiSubscriptions = getBrowse("FEsubscriptions")
    suspend fun getSubscriptions(continuation: String): ApiSubscriptionsContinuation = getBrowse("FEsubscriptions", continuation)

    suspend fun getTrending(): ApiTrending = getBrowse("FEtrending")
    suspend fun getTrending(continuation: String): ApiTrendingContinuation = getBrowse("FEtrending", continuation)

    suspend fun getChannel(id: String, params: String? = null): ApiChannel = getBrowse(
        browseId = id,
        params = params
    )

    suspend fun getSearchSuggestions(search: String): JsonElement = withContext(Dispatchers.IO) {
        val body = httpClient.get("https://suggestqueries-clients6.youtube.com/complete/search") {
            parameter("client", "youtube")
            parameter("ds", "yt")
            parameter("q", search)
        }.bodyAsText()

        json.parseToJsonElement(body.substringAfter("(").substringBeforeLast(")"))
    }

    suspend fun getPlayer(id: String): ApiPlayer = post(
        endpoint = "player",
        body = PlayerBody(
            context = innerTubeContext,
            videoId = id
        )
    ).body()

    suspend fun getNext(id: String): ApiNext = post(
        endpoint = "next",
        body = NextBody(
            context = innerTubeContext,
            videoId = id
        )
    ).body()

    suspend fun getNext(id: String, continuation: String): ApiNextContinuation = post(
        endpoint = "next",
        body = NextBody(
            context = innerTubeContext,
            videoId = id,
            continuation = continuation
        )
    ).body()

    suspend fun getSearchResults(query: String): ApiSearch = post(
        endpoint = "search",
        body = SearchBody(
            context = innerTubeContext,
            query = query
        )
    ).body()

    suspend fun getSearchResults(query: String, continuation: String): ApiSearchContinuation = post(
        endpoint = "search",
        body = SearchBody(
            context = innerTubeContext,
            query = query,
            continuation = continuation
        )
    ).body()

    suspend fun getTag(tag: String): ApiTag = getBrowse(
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

    suspend fun getTagContinuation(continuation: String): ApiTagContinuation = getBrowse(
        browseId = "FEhashtag",
        continuation = continuation
    )

    suspend fun createComment(text: String, params: String) {
        post(
            endpoint = "create_comment",
            body = CommentBody(
                context = innerTubeContext,
                commentText = text,
                createCommentParams = params
            )
        )
    }

    companion object {
        private const val YOUTUBE_URL = "https://www.youtube.com"
        private const val API_URL = "https://www.youtube.com/youtubei/v1"

        private const val CLIENT_NAME = "ANDROID"
        private const val CLIENT_VERSION = "17.11.37"
        private const val PLATFORM = "MOBILE"
        private const val FORM_FACTOR = "SMALL_FORM_FACTOR"

        fun getVideoThumbnail(id: String) = "https://i.ytimg.com/vi/$id/hqdefault.jpg"
    }
}

@Serializable
private data class InnerTubeData(
    @SerialName("INNERTUBE_API_KEY") val innerTubeApiKey: String,
    @SerialName("INNERTUBE_CONTEXT") val innerTubeContext: ApiContext
)