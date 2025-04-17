package net.dankito.dav

import net.dankito.web.client.WebClientResponse
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
sealed class DavResult<out T : Any, R>(
    val response: WebClientResponse<R>
) {

    fun isSuccess(): Boolean {
        contract {
            returns(true) implies (this@DavResult is Success<T, R>)
        }
        return this is Success
    }

    fun isFailure(): Boolean {
        contract {
            returns(true) implies (this@DavResult is Failure<T, R>)
        }
        return this is Failure
    }
}

class Success<out T : Any, R>(val data: T, response: WebClientResponse<R>) : DavResult<T, R>(response) {
    override fun toString() = "Success: $data"
}

class Failure<out T : Any, R>(val exception: Throwable?, response: WebClientResponse<R>) : DavResult<T, R>(response) {
    constructor(response: WebClientResponse<R>) : this(response.error, response)

    override fun toString() = "Failure: $exception"
}