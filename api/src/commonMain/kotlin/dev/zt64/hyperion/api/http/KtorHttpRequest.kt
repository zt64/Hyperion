package dev.zt64.hyperion.api.http

import com.google.api.client.http.LowLevelHttpRequest
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class KtorHttpRequest(private val statement: HttpStatement) : LowLevelHttpRequest() {
    override fun addHeader(name: String, value: String) {
    }

    override fun execute(): KtorHttpResponse {
        return runBlocking(Dispatchers.IO) {
            KtorHttpResponse(statement.execute())
        }
    }
}