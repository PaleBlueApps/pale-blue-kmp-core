package com.paleblueapps.kmpcore.apimanager

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class RealApiManager(
    private val baseUrl: String,
    private val client: HttpClient
): ApiManager {

    override suspend fun request(
        endpoint: Endpoint,
        body: Any?,
        queryParameters: Map<String, String>,
        headers: List<Pair<String, String>>,
        contentType: ContentType?,
        additional: HttpRequestBuilder.() -> Unit,
    ): Result<HttpResponse> = withContext(Dispatchers.IO) {
        val httpRequest = HttpRequestBuilder().apply {
            method = endpoint.method
            endpoint(path = endpoint.path)
            setBody(body)
            parametersMapOf(queryParameters)
            headerListOf(headers)
            contentType?.let { contentType(it) }
            additional()
        }
        runCatching { client.request(httpRequest) }
    }

    override fun invalidateBearerTokens() {
        client.authProvider<BearerAuthProvider>()?.clearToken()
    }

    private fun HttpRequestBuilder.endpoint(path: String) {
        val urlString = baseUrl.appendPath(path)
        url(urlString = urlString)
    }

    private fun HttpRequestBuilder.parametersMapOf(parameters: Map<String, String>) {
        parameters.forEach { (key, value) -> parameter(key, value) }
    }

    private fun HttpRequestBuilder.headerListOf(headers: List<Pair<String, String>>) {
        headers.forEach { (key, value) -> header(key, value) }
    }
}

private fun String.appendPath(path: String): String {
    val endWithSlash = this.endsWith("/")
    val startWithSlash = path.startsWith("/")

    return when {
        endWithSlash && startWithSlash -> this.removeSuffix("/") + path
        !endWithSlash && !startWithSlash -> "$this/$path"
        else -> this + path
    }
}
