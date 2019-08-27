package com.revolut.ledger.rest.response

object Responses {
    val notFoundError = ErrorObject(
        errorCode = 0,
        errorDetails = "Not found"
    )

    val badMethodError = ErrorObject(
        errorCode = 1,
        errorDetails = "Not supported method"
    )
}
