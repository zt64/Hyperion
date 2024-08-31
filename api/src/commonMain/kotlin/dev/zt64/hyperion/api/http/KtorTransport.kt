package dev.zt64.hyperion.api.http

import com.google.api.client.http.HttpTransport
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.request.prepareRequest
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class KtorTransport(private val engineFactory: HttpClientEngineFactory<*>) : HttpTransport() {
    private val client: HttpClient by lazy {
        HttpClient(engineFactory)
    }

    override fun buildRequest(method: String, url: String): KtorHttpRequest {
        return runBlocking(Dispatchers.IO) {
            val req = client.prepareRequest(url) {
                this.method = HttpMethod.parse(method)
            }

            KtorHttpRequest(req)
        }
    }
}