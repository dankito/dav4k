package net.dankito.dav.webdav.operations

import net.dankito.dav.DavResult
import net.dankito.web.client.ContentTypes
import net.dankito.web.client.RequestParameters
import net.dankito.web.client.WebClient
import net.dankito.dav.webdav.options.UploadFileOptions

open class UploadFileCommand(webClient: WebClient) : CommandBase(webClient) {

    open suspend fun uploadFile(destinationUrl: String, fileContent: ByteArray, options: UploadFileOptions? = null): DavResult<Boolean, Unit> {
        val headers = buildMap {
            put("Content-Length", fileContent.size.toString())

            if (options?.overwrite != true) {
                put("If-None-Match", "*") // TODO: does not work (at least not with my local WebDAV server)
            }
        }

        val request = RequestParameters(destinationUrl, Unit::class, fileContent, options?.contentType, ContentTypes.XML, headers)

        val response = webClient.put(request)

        return toBooleanResult(response)
    }

}