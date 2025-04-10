package net.dankito.dav.webdav.operations

import net.dankito.dav.web.KtorWebClient
import net.dankito.dav.web.RequestParameters
import net.dankito.dav.web.WebClient
import net.dankito.dav.webdav.operations.PropFindHandler.Companion.PropFindHttpMethod

abstract class CommandBase(
    protected val webClient: WebClient,
) {

    protected open suspend fun <T : Any> executeCustomRequest(request: RequestParameters<T>) =
        if (webClient is KtorWebClient) webClient.custom(PropFindHttpMethod, request)
        else webClient.custom(PropFindHttpMethod.value, request)

}