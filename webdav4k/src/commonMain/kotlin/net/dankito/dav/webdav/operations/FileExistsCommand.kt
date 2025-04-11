package net.dankito.dav.webdav.operations

import net.dankito.dav.DavResult
import net.dankito.dav.web.WebClient
import net.dankito.dav.web.head

open class FileExistsCommand(webClient: WebClient) : CommandBase(webClient) {

    open suspend fun exists(fileUrl: String): DavResult<Boolean, Unit> {
        val response = webClient.head(fileUrl)

        return toBooleanResult(response)
    }

}