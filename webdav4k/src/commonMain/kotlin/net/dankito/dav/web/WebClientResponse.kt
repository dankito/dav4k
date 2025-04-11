package net.dankito.dav.web

open class WebClientResponse<out T>(
    open val successful: Boolean,
    open val url: String,
    open val responseDetails: ResponseDetails? = null,
    open val error: Throwable? = null,
    open val body: T? = null
) {

    val statusCode = responseDetails?.statusCode ?: -1

    open fun getHeaderValue(headerName: String): String? {
        val headerNameLowerCased = headerName.lowercase() // header names are case insensitive, so compare them lower cased

        responseDetails?.headers?.let { headers ->
            headers.keys.forEach {
                if(it.lowercase() == headerNameLowerCased) {
                    return headers[it]?.firstOrNull()
                }
            }
        }

        return null
    }


    open val isInformationalResponse: Boolean
        get() = statusCode >= 100 && statusCode < 200

    open val isSuccessResponse: Boolean
        get() = statusCode >= 200 && statusCode < 300

    open val isRedirectionResponse: Boolean
        get() = statusCode >= 300 && statusCode < 400

    open val isClientErrorResponse: Boolean
        get() = statusCode >= 400 && statusCode < 500

    open val isServerErrorResponse: Boolean
        get() = statusCode >= 500 && statusCode < 600

    open val retryAfterSeconds: Int? by lazy {
        getHeaderValue("Retry-After")?.toIntOrNull()
    }


    override fun toString(): String {
        return if (successful) {
            "Successful: $statusCode $body"
        } else {
            "Error: $statusCode $error"
        }
    }

}