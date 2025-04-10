package net.dankito.dav.webdav.operations

import net.dankito.dav.web.RequestParameters
import net.dankito.dav.web.WebClient

open class DownloadFileCommand(webClient: WebClient) : CommandBase(webClient) {

    open suspend fun downloadFile(fileUrl: String): ByteArray? {
        val request = RequestParameters(fileUrl, ByteArray::class)

        val response = webClient.get(request)

        return if (response.isSuccessResponse) {
            response.body
        } else {
            null
        }
    }

}