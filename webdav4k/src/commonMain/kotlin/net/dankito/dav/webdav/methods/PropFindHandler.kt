package net.dankito.dav.webdav.methods

import io.ktor.http.*
import net.dankito.dav.web.*

open class PropFindHandler(
    protected val webClient: WebClient,
) {

    companion object {
        val PropFindHttpMethod = HttpMethod("PROPFIND")

        /**
         * The default is to return only the resource specified by url.
         */
        val DefaultDepth = 0

        val AllPropBody = """
            <d:propfind xmlns:d="DAV:">
              <d:allprop/>
            </d:propfind>
        """.trimIndent()

        val PropNameBody = """
            <d:propfind xmlns:d="DAV:">
              <d:propname/>
            </d:propfind>
        """.trimIndent()
    }


    /**
     * Returns all standard / DAV-defined properties.
     */
    open suspend fun allProp(url: String, depth: Int = DefaultDepth) = makeRequest(url, depth, AllPropBody)

    /**
     * Return just the property names (no values).
     */
    open suspend fun propName(url: String, depth: Int = DefaultDepth) = makeRequest(url, depth, PropNameBody)


    protected open suspend fun makeRequest(url: String, depth: Int, body: String?): String? {
        val request = RequestParameters(url, String::class, body, ContentTypes.XML, ContentTypes.XML, mapOf(
            "DEPTH" to if (depth in 0 until Int.MAX_VALUE) depth.toString() else "infinity"
        ))

        val response = if (webClient is KtorWebClient) webClient.custom(PropFindHttpMethod, request)
                        else webClient.custom(PropFindHttpMethod.value, request)

        return response.body
    }

}