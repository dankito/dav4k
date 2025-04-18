package net.dankito.dav.webdav.operations

import net.dankito.dav.DavResult
import net.dankito.dav.Failure
import net.dankito.dav.Success
import net.dankito.web.client.RequestParameters
import net.dankito.web.client.WebClient
import net.dankito.web.client.WebClientResult

abstract class CommandBase(
    protected val webClient: WebClient,
) {

    protected open suspend fun <T : Any> executeCustomRequest(method: String, request: RequestParameters<T>) =
        webClient.custom(method, request)


    protected open fun toBooleanResult(response: WebClientResult<Unit>): DavResult<Boolean, Unit> =
        if (response.successful == false) Failure(response)
        else Success(true, response)

    protected open fun <T: Any> toResult(response: WebClientResult<T>): DavResult<T, T> =
        if (response.successful == false || response.body == null) Failure(response)
        else Success(response.body!!, response)

}