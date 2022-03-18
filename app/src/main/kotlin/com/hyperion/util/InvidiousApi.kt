package com.hyperion.util

import androidx.compose.ui.text.intl.Locale
import com.hyperion.model.Channel
import com.hyperion.model.TrendingVideo
import com.hyperion.model.Video
import com.hyperion.model.VideoComments
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*

object InvidiousApi {
    private const val baseUrl = "https://vid.puffyan.us/api/v1"

    private val httpClient = HttpClient(Android) {
        install(JsonFeature)
    }

    suspend fun getTrending(region: String = Locale.current.region): List<TrendingVideo> = httpClient.get("$baseUrl/trending") {
        parameter("region", region)
    }
    suspend fun getVideo(videoId: String): Video = httpClient.get("$baseUrl/videos/$videoId")
    suspend fun getChannel(channelId: String): Channel = httpClient.get("$baseUrl/channels/$channelId")
    suspend fun getComments(videoId: String, continuation: String?): VideoComments =
        httpClient.get("$baseUrl/channels/$videoId") {
            parameter("sort_by", "top")
            parameter("continuation", continuation)
        }
}