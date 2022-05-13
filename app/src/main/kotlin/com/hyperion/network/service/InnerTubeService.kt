package com.hyperion.network.service

import com.hyperion.network.body.BrowseBody
import com.hyperion.network.body.NextBody
import com.hyperion.network.body.PlayerBody
import com.hyperion.network.body.SearchBody
import com.hyperion.network.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import javax.inject.Inject

class InnerTubeService @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
) {
    private lateinit var innerTubeApiKey: String
    private lateinit var innerTubeContext: ApiContext

    suspend fun initApi() {
        val html = httpClient.get(YOUTUBE_URL) {
            parameter("hl", "en")
        }.bodyAsText()

        val (obj) = "ytcfg.set\\((.*?)\\);".toRegex(RegexOption.DOT_MATCHES_ALL)
            .findAll(html)
            .elementAt(1)
            .destructured

        val data: InnerTubeData = json.decodeFromString(obj)

        innerTubeApiKey = data.innerTubeApiKey
        innerTubeContext = data.innerTubeContext.copy(
            client = data.innerTubeContext.client.copy(
                clientName = CLIENT_NAME,
                clientVersion = CLIENT_VERSION,
                platform = PLATFORM
            )
        )
    }

    // TODO
    suspend fun getRecommendations() = httpClient.post("$API_URL/browse") {
        parameter("key", innerTubeApiKey)
        contentType(ContentType.Application.Json)
        setBody(
            BrowseBody(
                context = innerTubeContext,
                browseId = "FEwhat_to_watch"
            )
        )
    }

    suspend fun getTrending(continuation: String? = null) = httpClient.post("$API_URL/browse") {
        parameter("key", innerTubeApiKey)
        contentType(ContentType.Application.Json)
        setBody(
            BrowseBody(
                context = innerTubeContext,
                browseId = "FEtrending",
                continuation = continuation
            )
        )
    }.body<ApiTrending>()

    suspend fun getChannel(id: String) = httpClient.post("$API_URL/browse") {
        parameter("key", innerTubeApiKey)
        contentType(ContentType.Application.Json)
        setBody(
            BrowseBody(
                context = innerTubeContext,
                browseId = id
            )
        )
    }.body<ApiChannel>()

    suspend fun getSuggestions(search: String): JsonElement {
        val body = httpClient.get("https://suggestqueries-clients6.youtube.com/complete/search") {
            parameter("client", "youtube")
            parameter("ds", "yt")
            parameter("q", search)
        }.bodyAsText()

        return json.parseToJsonElement(body.substringAfter("(").substringBeforeLast(")"))
    }

    suspend fun search(query: String) = httpClient.post("$API_URL/search") {
        parameter("key", innerTubeApiKey)
        contentType(ContentType.Application.Json)
        setBody(
            SearchBody(
                context = innerTubeContext,
                query = query
            )
        )
    }.body<ApiSearch>()

    suspend fun getPlayer(id: String) = httpClient.post("$API_URL/player") {
        parameter("key", innerTubeApiKey)
        contentType(ContentType.Application.Json)
        setBody(
            PlayerBody(
                context = innerTubeContext,
                videoId = id
            )
        )
    }.body<ApiPlayer>()

    suspend fun getNext(id: String, continuation: String? = null) = httpClient.post("$API_URL/next") {
        parameter("key", innerTubeApiKey)
        contentType(ContentType.Application.Json)
        setBody(
            NextBody(
                context = innerTubeContext,
                videoId = id,
                continuation = continuation
            )
        )
    }.body<ApiNext>()

    private companion object {
        private const val YOUTUBE_URL = "https://www.youtube.com"
        private const val API_URL = "https://www.youtube.com/youtubei/v1"

        private const val CLIENT_NAME = "ANDROID"
        private const val CLIENT_VERSION = "17.11.37"
        private const val PLATFORM = "MOBILE"
    }
}

@Serializable
private data class InnerTubeData(
    @SerialName("INNERTUBE_API_KEY")
    val innerTubeApiKey: String,
    @SerialName("INNERTUBE_CONTEXT")
    val innerTubeContext: ApiContext,
)