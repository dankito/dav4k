package net.dankito.dav.webdav.operations

import io.ktor.http.*
import net.dankito.dav.web.RequestParameters
import net.dankito.dav.web.WebClient

open class MoveFileCommand(webClient: WebClient) : CommandBase(webClient) {

    companion object {
        val MoveHttpMethod = HttpMethod("MOVE")
    }


    open suspend fun move(sourceUrl: String, destinationUrl: String, overwrite: Boolean = false): Boolean {
        val headers = buildMap {
            put("Destination", destinationUrl)
            put("Overwrite", if (overwrite) "T" else "F")
        }
        val request = RequestParameters(sourceUrl, Unit::class, headers = headers)

        val response = executeCustomRequest(MoveHttpMethod, request)

        // ChatGPT says that it might return a MultiStatus response if copying/moving recursively and some resources
        // fail, but haven't found any example for this.
        return response.isSuccessResponse
    }

}