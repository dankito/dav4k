package net.dankito.dav.webdav.operations

import io.ktor.http.*
import net.dankito.dav.web.RequestParameters
import net.dankito.dav.web.WebClient

open class CreateDirectoryCommand(webClient: WebClient) : CommandBase(webClient) {

    companion object {
        val MkColHttpMethod = HttpMethod("MKCOL")
    }


    open suspend fun createDirectory(url: String): Boolean {
        val request = RequestParameters(url, Unit::class)

        val response = executeCustomRequest(MkColHttpMethod, request)

        return response.isSuccessResponse
    }

}