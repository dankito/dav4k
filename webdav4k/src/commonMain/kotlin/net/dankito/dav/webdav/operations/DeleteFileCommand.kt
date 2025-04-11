package net.dankito.dav.webdav.operations

import net.dankito.dav.DavResult
import net.dankito.dav.web.RequestParameters
import net.dankito.dav.web.WebClient

open class DeleteFileCommand(webClient: WebClient) : CommandBase(webClient) {

    open suspend fun deleteFile(fileUrl: String): DavResult<Boolean, Unit> {
        val request = RequestParameters(fileUrl, Unit::class)

        val response = webClient.delete(request)

        // ChatGPT says that it might return a MultiStatus response for recursive deletes, may list per-resource status,
        // but haven't found any example for this.
        return toBooleanResult(response)
    }

}