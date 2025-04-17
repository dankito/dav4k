package net.dankito.dav.webdav.operations

import net.dankito.dav.DavResult
import net.dankito.web.client.RequestParameters
import net.dankito.web.client.WebClient

open class DownloadFileCommand(webClient: WebClient) : CommandBase(webClient) {

    open suspend fun downloadFile(fileUrl: String): DavResult<ByteArray, ByteArray> {
        val request = RequestParameters(fileUrl, ByteArray::class)

        val response = webClient.get(request)

        return toResult(response)
    }

}