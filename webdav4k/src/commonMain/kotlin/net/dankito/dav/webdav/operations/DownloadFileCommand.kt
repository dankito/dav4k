package net.dankito.dav.webdav.operations

import net.dankito.dav.DavResult
import net.dankito.dav.web.RequestParameters
import net.dankito.dav.web.WebClient

open class DownloadFileCommand(webClient: WebClient) : CommandBase(webClient) {

    open suspend fun downloadFile(fileUrl: String): DavResult<ByteArray, ByteArray> {
        val request = RequestParameters(fileUrl, ByteArray::class)

        val response = webClient.get(request)

        return toResult(response)
    }

}