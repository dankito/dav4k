package net.dankito.dav.webdav.operations

import net.dankito.dav.DavResult
import net.dankito.web.client.RequestParameters
import net.dankito.web.client.WebClient

open class MoveFileCommand(webClient: WebClient) : CommandBase(webClient) {

    companion object {
        val MoveHttpMethod = "MOVE"
    }


    open suspend fun move(sourceUrl: String, destinationUrl: String, overwrite: Boolean = false): DavResult<Boolean, Unit> {
        val headers = buildMap {
            put("Destination", destinationUrl)
            put("Overwrite", if (overwrite) "T" else "F")
        }
        val request = RequestParameters(sourceUrl, Unit::class, headers = headers)

        val response = executeCustomRequest(MoveHttpMethod, request)

        // ChatGPT says that it might return a MultiStatus response if copying/moving recursively and some resources
        // fail, but haven't found any example for this.
        return toBooleanResult(response)
    }

}