package net.dankito.dav

import net.dankito.web.client.WebClientResult
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
sealed class DavResult<out T : Any, R>(
    val response: WebClientResult<R>
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

class Success<out T : Any, R>(val data: T, response: WebClientResult<R>) : DavResult<T, R>(response) {
    override fun toString() = "Success: $data"
}

class Failure<out T : Any, R>(val exception: Throwable?, response: WebClientResult<R>) : DavResult<T, R>(response) {
    constructor(response: WebClientResult<R>) : this(response.error, response)

    override fun toString() = "Failure: $exception"
}