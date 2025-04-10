package net.dankito.dav.webdav.operations

import io.ktor.http.*
import net.dankito.dav.DefaultNamespaces
import net.dankito.dav.web.*
import net.dankito.dav.webdav.model.MultiStatus
import net.dankito.dav.webdav.model.Property

open class PropFindHandler(
    protected val webClient: WebClient,
    protected val multiStatusReader: MultiStatusReader = MultiStatusReader.Instance
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

    /**
     * Returns only the listed properties.
     */
    open suspend fun prop(url: String, vararg props: Property) = prop(url, DefaultDepth, *props)

    /**
     * Returns only the listed properties.
     */
    open suspend fun prop(url: String, depth: Int = DefaultDepth, vararg props: Property) =
        makeRequest(url, depth, createPropBody(props.toList()))

    protected open fun createPropBody(properties: List<Property>): String {
        val namespaces = properties.associate { it.namespaceUri to (it.prefix ?: getDefaultNamespacePrefix(it.namespaceUri)) }
            .toMutableMap().apply { put(DefaultNamespaces.Dav, DefaultNamespaces.DavPrefix) }
        // TODO: what to use when prefix is null? Throw an exception?
        val namespaceList = namespaces.entries.joinToString(" ") { "xmlns:${it.value}=\"${it.key}\"" }

        // TODO: create with an XML library
        return """
            <d:propfind $namespaceList>
              <d:prop>
                ${properties.joinToString("\n") { "<${namespaces[it.namespaceUri]}:${it.name} />" } }
              </d:prop>
            </d:propfind>
        """.trimIndent()
    }

    protected open fun getDefaultNamespacePrefix(namespaceUri: String?): String? =
        namespaceUri?.let { DefaultNamespaces.getPrefixForNamespace(namespaceUri) }


    protected open suspend fun makeRequest(url: String, depth: Int, body: String?): MultiStatus? {
        val request = RequestParameters(url, String::class, body, ContentTypes.XML, ContentTypes.XML, mapOf(
            "DEPTH" to if (depth in 0 until Int.MAX_VALUE) depth.toString() else "infinity"
        ))

        val response = if (webClient is KtorWebClient) webClient.custom(PropFindHttpMethod, request)
                        else webClient.custom(PropFindHttpMethod.value, request)

        return if (response.isSuccessResponse) {
            multiStatusReader.parse(response.body!!)
        } else {
            null // TODO: return error
        }
    }

}