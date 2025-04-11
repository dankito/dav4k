package net.dankito.dav.web

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.codinux.log.logger
import net.dankito.dav.web.credentials.Credentials

open class KtorWebClient(
    baseUrl: String? = null,
    credentials: Credentials? = null,
    defaultUserAgent: String? = RequestParameters.DefaultMobileUserAgent
) : WebClient {

    protected open val json = Json {
        ignoreUnknownKeys = true
    }

    protected val log by logger()

    protected open val client = HttpClient { configureClient(this, baseUrl, credentials, defaultUserAgent) }

    private fun configureClient(config: HttpClientConfig<*>, baseUrl: String?, credentials: Credentials?, defaultUserAgent: String?) {
        config.apply {
            install(HttpTimeout)
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                (credentials as? net.dankito.dav.web.credentials.BasicAuthCredentials)?.let { basicAuth ->
                    basic {
                        sendWithoutRequest { true }
                        credentials {
                            BasicAuthCredentials(basicAuth.username, basicAuth.password)
                        }
                    }
                }
            }

            defaultRequest {
                baseUrl?.let {
                    url(baseUrl)
                }

                defaultUserAgent?.let {
                    userAgent(it)
                }
            }
        }
    }


    override suspend fun <T : Any> get(parameters: RequestParameters<T>) =
        makeRequest(HttpMethod.Get, parameters)

    override suspend fun head(parameters: RequestParameters<Unit>) =
        makeRequest(HttpMethod.Head, parameters)

    override suspend fun <T : Any> post(parameters: RequestParameters<T>) =
        makeRequest(HttpMethod.Post, parameters)

    override suspend fun <T : Any> put(parameters: RequestParameters<T>) =
        makeRequest(HttpMethod.Put, parameters)

    override suspend fun <T : Any> delete(parameters: RequestParameters<T>) =
        makeRequest(HttpMethod.Delete, parameters)

    override suspend fun <T : Any> custom(httpMethod: String, parameters: RequestParameters<T>) =
        custom(HttpMethod(httpMethod), parameters)

    open suspend fun <T : Any> custom(method: HttpMethod, parameters: RequestParameters<T>) =
        makeRequest(method, parameters)


    protected open suspend fun <T : Any> makeRequest(method: HttpMethod, parameters: RequestParameters<T>): WebClientResponse<T> {
        return try {
            val httpResponse = client.request {
                configureRequest(this, method, parameters)
            }

            mapHttResponse(method, parameters, httpResponse)
        } catch (e: Throwable) {
            log.error(e) { "Error during request to ${method.value} ${parameters.url}" }
            // be aware this might not be the absolute url but only the relative url the user has passed to WebClient
            WebClientResponse(false, parameters.url, error = WebClientException(-1, emptyMap(), e.message ?: "", e))
        }
    }

    protected open suspend fun <T : Any> configureRequest(builder: HttpRequestBuilder, method: HttpMethod, parameters: RequestParameters<T>) {
        builder.apply {
            this.method = method

            url {
                val url = parameters.url
                if (url.startsWith("http", true)) { // absolute url
                    takeFrom(url)
                } else { // relative url
                    appendPathSegments(url)
                }

                parameters.queryParameters.forEach { (name, value) -> this.parameters.append(name, value.toString()) }
            }

            parameters.headers.forEach { (name, value) ->
                this.headers.append(name, value)
            }

            parameters.userAgent?.let {
                this.userAgent(it)
            }

            parameters.accept?.let {
                this.accept(ContentType.parse(it))
            }

            timeout {
                // JS doesn't support connectTimeout and socketTimeout
                connectTimeoutMillis = parameters.connectTimeoutMillis
                socketTimeoutMillis = parameters.socketTimeoutMillis
                requestTimeoutMillis = parameters.requestTimeoutMillis
            }

            parameters.body?.let {
                contentType(parameters.contentType?.let { ContentType.parse(it) } ?: ContentType.Application.Json)

                setBody(it)
            }
        }
    }

    protected open suspend fun <T : Any> mapHttResponse(method: HttpMethod, parameters: RequestParameters<T>, httpResponse: HttpResponse): WebClientResponse<T> {
        val statusCode = httpResponse.status.value
        val headers = httpResponse.headers.toMap()
        val url = httpResponse.request.url.toString()

        return if (httpResponse.status.isSuccess()) {
            try {
                WebClientResponse(true, url, statusCode, headers, body = decodeResponse(parameters, httpResponse))
            } catch (e: Throwable) {
                log.error(e) { "Error while mapping response of: ${method.value} ${httpResponse.request.url}, ${httpResponse.headers.toMap()}" }
                WebClientResponse(true, url, statusCode, headers, WebClientException(statusCode, headers, e.message ?: "", e))
            }
        } else {
            val responseBody = httpResponse.bodyAsText()

            WebClientResponse(false, url, statusCode, headers, WebClientException(statusCode, headers, responseBody))
        }
    }

    @Suppress("UNCHECKED_CAST")
    @OptIn(InternalSerializationApi::class)
    protected open suspend fun <T : Any> decodeResponse(parameters: RequestParameters<T>, clientResponse: HttpResponse): T {
        val responseClass = parameters.responseClass

        return if (responseClass == null || responseClass == Unit::class) {
            Unit as T
        } else if(responseClass == String::class) {
            clientResponse.bodyAsText() as T
        } else if (responseClass == ByteArray::class) {
            val bytes: ByteArray = clientResponse.body()
            bytes as T
        } else {
            // TODO: add cache for Serializers
            // TODO: stream response (at least on JVM)
            json.decodeFromString(responseClass.serializer(), clientResponse.body())
        }
    }
}