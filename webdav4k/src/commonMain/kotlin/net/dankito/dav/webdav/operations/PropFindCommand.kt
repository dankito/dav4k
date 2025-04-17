package net.dankito.dav.webdav.operations

import io.ktor.http.*
import net.dankito.dav.DavResult
import net.dankito.dav.DefaultNamespaces
import net.dankito.dav.Failure
import net.dankito.dav.Success
import net.dankito.web.client.*
import net.dankito.dav.webdav.model.*

open class PropFindCommand(
    webClient: WebClient,
    protected val multiStatusReader: MultiStatusReader = MultiStatusReader.Instance
) : CommandBase(webClient) {

    companion object {
        val PropFindHttpMethod = HttpMethod("PROPFIND")

        /**
         * The default is to return only the resource specified by url.
         */
        val DefaultDepth = Depth.ResourceOnly

        // see e.g. https://www.rfc-editor.org/rfc/rfc4918#section-9.1.5
        val AllPropBody = """
            <d:propfind xmlns:d="DAV:">
              <d:allprop/>
            </d:propfind>
        """.trimIndent()

        // see e.g. https://www.rfc-editor.org/rfc/rfc4918#section-9.1.4
        val PropNameBody = """
            <d:propfind xmlns:d="DAV:">
              <d:propname/>
            </d:propfind>
        """.trimIndent()


        fun DavResult<List<DavResource>, String>.asSingleResource(): DavResult<DavResource, String> {
            val response = this.response

            return if (this.isSuccess()) {
                val data = this.data as List<DavResource> // why doesn't the compiler get that data is of type List<DavResource>?
                if (data.isNotEmpty()) Success(data.first(), response)
                else Failure(response)
            }
            else if (this.isFailure()) Failure(this.exception, response)
            else Failure(response)
        }
    }


    /**
     * Returns all standard / DAV-defined properties.
     */
    open suspend fun allProp(url: String, depth: Depth = DefaultDepth) = makeRequest(url, depth, AllPropBody)

    /**
     * Return just the property names (no values).
     */
    open suspend fun propName(url: String, depth: Depth = DefaultDepth) = makeRequest(url, depth, PropNameBody)

    /**
     * Returns only the listed properties.
     */
    open suspend fun prop(url: String, vararg props: Property) = prop(url, DefaultDepth, *props)

    /**
     * Returns only the listed properties.
     */
    open suspend fun prop(url: String, depth: Depth = DefaultDepth, vararg props: Property) =
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


    protected open suspend fun makeRequest(url: String, depth: Depth, body: String?): DavResult<List<DavResource>, String> {
        val request = RequestParameters(url, String::class, body, ContentTypes.XML, ContentTypes.XML, mapOf(
            "DEPTH" to depth.apiValue
        ))

        val response = executeCustomRequest(PropFindHttpMethod, request)

        return if (response.isSuccessResponse) {
            val (error, multiStatus) = multiStatusReader.parse(response.body!!)
            if (multiStatus != null) {
                Success(multiStatus.responses.map { mapToResource(it) }, response)
            } else {
                Failure(error, response)
            }
        } else {
            Failure(response)
        }
    }

    protected open fun mapToResource(response: Response): DavResource {
        val successResponses = response.propStats.filter { it.status?.isSuccess == true } // there should be only one success PropStats per Response
        val errorResponses = response.propStats.filter { it.status?.isSuccess == false } // and may one error PropStats for requested properties that have not been found

        return DavResource(response.href.first(), successResponses.flatMap { it.properties }, errorResponses.flatMap { it.properties })
    }

}