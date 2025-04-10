package net.dankito.dav.webdav.operations

import net.dankito.dav.web.ContentTypes
import net.dankito.dav.web.RequestParameters
import net.dankito.dav.web.WebClient
import net.dankito.dav.webdav.options.UploadFileOptions

open class UploadFileCommand(webClient: WebClient) : CommandBase(webClient) {

    open suspend fun uploadFile(destinationUrl: String, fileContent: ByteArray, options: UploadFileOptions? = null): Boolean {
        val headers = buildMap {
            put("Content-Length", fileContent.size.toString())

            if (options?.overwrite != true) {
                put("If-None-Match", "*") // TODO: does not work (at least not with my local WebDAV server)
            }
        }

        val request = RequestParameters(destinationUrl, String::class, fileContent, options?.contentType, ContentTypes.XML, headers)

        val response = webClient.put(request)

        return response.isSuccessResponse
    }

}