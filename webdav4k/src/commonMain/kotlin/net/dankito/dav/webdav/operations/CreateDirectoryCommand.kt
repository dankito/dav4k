package net.dankito.dav.webdav.operations

import io.ktor.http.*
import net.dankito.dav.DavResult
import net.dankito.web.client.RequestParameters
import net.dankito.web.client.WebClient

open class CreateDirectoryCommand(webClient: WebClient) : CommandBase(webClient) {

    companion object {
        val MkColHttpMethod = HttpMethod("MKCOL")
    }


    open suspend fun createDirectory(url: String): DavResult<Boolean, Unit> {
        val request = RequestParameters(url, Unit::class)

        val response = executeCustomRequest(MkColHttpMethod, request)

        return toBooleanResult(response)
    }

}