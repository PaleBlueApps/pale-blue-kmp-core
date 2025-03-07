package com.paleblueapps.kmmcore.apimanager

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ApiManager(
    private val baseUrl: String,
    private val enableLogging: Boolean = false,
    private val requestTimeout: Duration = 30.seconds,
    private val socketTimeout: Duration = 30.seconds,
    private val connectTimeout: Duration = 30.seconds,
    private val defaultRequestConfig: DefaultRequest.DefaultRequestBuilder.() -> Unit = {},
    private val responseValidator: ResponseValidator = {},
) {
    private val client = HttpClient {
        install(DefaultRequest) {
            defaultRequestConfig()
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        if (enableLogging) {
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = requestTimeout.inWholeMilliseconds
            socketTimeoutMillis = socketTimeout.inWholeMilliseconds
            connectTimeoutMillis = connectTimeout.inWholeMilliseconds
        }

        HttpResponseValidator { validateResponse(responseValidator) }
    }

    suspend inline fun <reified Res : Any> call(
        endpoint: Endpoint,
        body: Any? = "",
        queryParameters: Map<String, String> = emptyMap(),
        headers: List<Pair<String, String>> = emptyList(),
        noinline additional: HttpRequestBuilder.() -> Unit = {},
    ): Result<Res> = withContext(Dispatchers.IO) {
        val result = request(
            endpoint = endpoint,
            body = body,
            queryParameters = queryParameters,
            headers = headers,
            additional = additional,
        )

        result.mapCatching { response ->
            val responseBody = response.body<Res>()
            responseBody
        }
    }

    suspend fun request(
        endpoint: Endpoint,
        body: Any? = "",
        queryParameters: Map<String, String> = emptyMap(),
        headers: List<Pair<String, String>> = emptyList(),
        additional: HttpRequestBuilder.() -> Unit = {},
    ): Result<HttpResponse> = withContext(Dispatchers.IO) {
        val httpRequest = HttpRequestBuilder().apply {
            method = endpoint.method
            endpoint(path = endpoint.path)
            contentType(ContentType.Application.Json)
            setBody(body)
            parametersMapOf(queryParameters)
            headerListOf(headers)
            additional()
        }
        runCatching { client.request(httpRequest) }
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
