package net.dankito.dav.webdav.operations

import io.ktor.http.*
import net.dankito.dav.web.KtorWebClient
import net.dankito.dav.web.RequestParameters
import net.dankito.dav.web.WebClient

abstract class CommandBase(
    protected val webClient: WebClient,
) {

    protected open suspend fun <T : Any> executeCustomRequest(method: HttpMethod, request: RequestParameters<T>) =
        if (webClient is KtorWebClient) webClient.custom(method, request)
        else webClient.custom(method.value, request)

}