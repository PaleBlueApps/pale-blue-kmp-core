package com.paleblueapps.kmpcore.apimanager

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.ResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingConfig
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * `ApiManager` is a utility class designed to simplify making HTTP requests to REST APIs using the Ktor HTTP client.
 * It provides a convenient way to configure the client, handle common tasks like serialization, logging, and timeouts,
 * and make requests with custom endpoints, bodies, query parameters, and headers.
 */
interface ApiManager {

    /**
     * Makes an asynchronous HTTP request to the specified endpoint.
     *
     * @param endpoint The [Endpoint] object defining the request's URL, method, and other details.
     * @param body The optional request body. Defaults to an empty string. Can be any object that Ktor can serialize.
     * @param queryParameters A map of query parameters to include in the request URL. Defaults to an empty map.
     * @param headers A list of headers to include in the request. Each header is represented as a pair of (name, value). Defaults to an empty list.
     * @param additional An optional lambda that can be used to add further customization to the [HttpRequestBuilder].
     *                   This allows setting things like request timeout or custom headers not covered by the `headers` parameter.
     *                   Defaults to an empty lambda (no additional customization).
     *
     * @return A [Result] object containing either the [HttpResponse] on success,
     *         or a [Throwable] representing an error that occurred during the request.
     *
     */
    suspend fun request(
        endpoint: Endpoint,
        body: Any? = "",
        queryParameters: Map<String, String> = emptyMap(),
        headers: List<Pair<String, String>> = emptyList(),
        additional: HttpRequestBuilder.() -> Unit = {},
    ): Result<HttpResponse>
}

/**
 * Makes an asynchronous HTTP request to the specified endpoint and attempts to deserialize the response body
 * to the specified type [Res].
 *
 * @param endpoint The [Endpoint] object defining the request's URL, method, and other details.
 * @param body The optional request body. Defaults to an empty string. Can be any object that Ktor can serialize.
 * @param queryParameters A map of query parameters to include in the request URL. Defaults to an empty map.
 * @param headers A list of headers to include in the request. Each header is represented as a pair of (name, value). Defaults to an empty list.
 * @param additional An optional lambda that can be used to add further customization to the [HttpRequestBuilder].
 *                   This allows setting things like request timeout or custom headers not covered by the `headers` parameter.
 *                   Defaults to an empty lambda (no additional customization).
 *
 * @return A [Result] object containing either the deserialized response body of type [Res] on success,
 *         or a [Throwable] representing an error that occurred during the request or deserialization.
 */
suspend inline fun <reified Res : Any> ApiManager.call(
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

/**
 * `ApiManager` is a utility class designed to simplify making HTTP requests to REST APIs using the Ktor HTTP client.
 * It provides a convenient way to configure the client, handle common tasks like serialization, logging, and timeouts,
 * and make requests with custom endpoints, bodies, query parameters, and headers.
 *
 * @property baseUrl The base URL for all API requests.
 * @property client The Ktor [HttpClient] instance to use for making requests. This allows for custom configurations and plugins to be applied.
 *
 * ## Usage example
 * ```
 * val apiManager = ApiManager(
 *     baseUrl = "https://api.example.com/",
 *     client = HttpClient {
 *        install(DefaultRequest) {
 *          header("X-ID-device", "android")
 *          header("X-API-Version", "1")
 *          contentType(ContentType.Application.Json)
 *        }
 *     }
 * )
 * ```
 */
fun ApiManager(
    baseUrl: String,
    client: HttpClient
): ApiManager = RealApiManager(
    baseUrl = baseUrl,
    client = client,
)


/**
 * `ApiManager` is a utility class designed to simplify making HTTP requests to REST APIs using the Ktor HTTP client.
 * It provides a convenient way to configure the client, handle common tasks like serialization, logging, and timeouts,
 * and make requests with custom endpoints, bodies, query parameters, and headers.
 *
 * @property baseUrl The base URL for all API requests.
 * @property enableLogging Whether to enable HTTP request/response logging. Defaults to `false`.
 * @property requestTimeout The maximum duration allowed for an entire request to complete, including sending the request and receiving the response. Defaults to 30 seconds.
 * @property socketTimeout The maximum duration allowed for reading data from the server after a connection has been established. Defaults to 30 seconds.
 * @property connectTimeout The maximum duration allowed for establishing a connection to the server. Defaults to 30 seconds.
 * @property defaultRequestConfig A lambda function to configure the `DefaultRequest` plugin. Useful for setting default headers, authentication, etc.
 * @property responseValidator A lambda function to validate the HTTP response.
 *
 * ## Usage example
 * ```
 * val apiManager = ApiManager(
 *     baseUrl = "https://api.example.com/",
 *     enableLogging = true,
 *     defaultRequestConfig = {
 *         header("X-ID-device", "android")
 *         header("X-API-Version", "1")
 *     },
 *     responseValidator = { response ->
 *         when (response.status.value) {
 *             in 200..299 -> {}
 *             401 -> throw Error.UnauthorizedError
 *             in 400..499 -> throw Error.BackendResponseError
 *             else -> throw Error.BackendError
 *         }
 *     },
 * )
 * ```
 */
fun ApiManager(
    baseUrl: String,
    enableLogging: Boolean = false,
    requestTimeout: Duration = 30.seconds,
    socketTimeout: Duration = 30.seconds,
    connectTimeout: Duration = 30.seconds,
    defaultRequestConfig: DefaultRequest.DefaultRequestBuilder.() -> Unit = {},
    responseValidator: ResponseValidator = {},
): ApiManager = RealApiManager(
    baseUrl = baseUrl,
    client = HttpClient {
        install(DefaultRequest.Plugin) {
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
)