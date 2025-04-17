package net.dankito.dav.webdav.operations

import io.ktor.http.*
import net.dankito.dav.DavResult
import net.dankito.dav.Failure
import net.dankito.dav.Success
import net.dankito.web.client.KtorWebClient
import net.dankito.web.client.RequestParameters
import net.dankito.web.client.WebClient
import net.dankito.web.client.WebClientResponse

abstract class CommandBase(
    protected val webClient: WebClient,
) {

    protected open suspend fun <T : Any> executeCustomRequest(method: HttpMethod, request: RequestParameters<T>) =
        if (webClient is KtorWebClient) webClient.custom(method, request)
        else webClient.custom(method.value, request)


    protected open fun toBooleanResult(response: WebClientResponse<Unit>): DavResult<Boolean, Unit> =
        if (response.error != null || response.isSuccessResponse == false) Failure(response)
        else Success(true, response)

    protected open fun <T: Any> toResult(response: WebClientResponse<T>): DavResult<T, T> =
        if (response.error != null || response.isSuccessResponse == false || response.body == null) Failure(response)
        else Success(response.body!!, response)

}