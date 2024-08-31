package dev.zt64.hyperion.api.http

import com.google.api.client.http.LowLevelHttpResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.InputStream

class KtorHttpResponse(private val response: HttpResponse) : LowLevelHttpResponse() {
    override fun getContent(): InputStream {
        return runBlocking(Dispatchers.IO) {
            response.bodyAsChannel().toInputStream()
        }
    }

    override fun getContentEncoding(): String {
        return response.headers["Content-Encoding"].orEmpty()
    }

    override fun getContentLength(): Long {
        return response.contentLength() ?: -1
    }

    override fun getContentType(): String {
        return response.contentType()!!.let {
            "${it.contentType}/${it.contentSubtype}"
        }
    }

    override fun getStatusLine(): String {
        return response.status.toString()
    }

    override fun getStatusCode(): Int {
        return response.status.value
    }

    override fun getReasonPhrase(): String {
        return response.status.description
    }

    override fun getHeaderCount(): Int {
        return response.headers.entries().count()
    }

    override fun getHeaderName(index: Int): String {
        return response.headers.names().elementAt(index)
    }

    override fun getHeaderValue(index: Int): String {
        return response.headers.entries().elementAt(index).value.firstOrNull()!!
    }
}