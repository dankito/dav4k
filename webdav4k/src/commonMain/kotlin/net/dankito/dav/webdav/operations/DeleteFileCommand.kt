package net.dankito.dav.webdav.operations

import net.dankito.dav.web.RequestParameters
import net.dankito.dav.web.WebClient
import net.dankito.dav.webdav.options.UploadFileOptions

open class DeleteFileCommand(webClient: WebClient) : CommandBase(webClient) {

    open suspend fun deleteFile(fileUrl: String): Boolean {
        val request = RequestParameters(fileUrl, Unit::class)

        val response = webClient.delete(request)

        return response.isSuccessResponse
    }

}