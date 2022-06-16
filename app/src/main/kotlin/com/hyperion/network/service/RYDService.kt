package com.hyperion.network.service

import com.hyperion.network.dto.ApiVotes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
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