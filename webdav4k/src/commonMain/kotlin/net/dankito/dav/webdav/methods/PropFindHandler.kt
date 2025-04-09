package net.dankito.dav.webdav.methods

import io.ktor.http.*
import net.dankito.dav.web.*

open class PropFindHandler(
    protected val webClient: WebClient,
) {

    companion object {
        val PropFindHttpMethod = HttpMethod("PROPFIND")
    }


    open suspend fun list(url: String, depth: Int = 1): String? {
        val body: String? = null
        val request = RequestParameters(url, String::class, body, ContentTypes.XML, ContentTypes.XML, mapOf(
            "DEPTH" to if (depth in 0 until Int.MAX_VALUE) depth.toString() else "infinity"
        ))

        val response = if (webClient is KtorWebClient) webClient.custom(PropFindHttpMethod, request)
                        else webClient.custom(PropFindHttpMethod.value, request)

        return response.body
    }

}