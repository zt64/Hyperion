package com.hyperion.network.service

import com.hyperion.network.dto.ApiVotes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RYDService(private val httpClient: HttpClient) {
    suspend fun getVotes(videoId: String): ApiVotes = withContext(Dispatchers.IO) {
        httpClient.get("$BASE_URL/votes") {
            parameter("videoId", videoId)
        }.body()
    }

    companion object {
        private const val BASE_URL = "https://returnyoutubedislikeapi.com"
    }
}