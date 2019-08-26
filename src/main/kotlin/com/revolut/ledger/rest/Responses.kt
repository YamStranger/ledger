package com.revolut.ledger.rest

object Responses {
    val notFoundError = ErrorResponse(
        errorCode = 0,
        errorDetails = "Not found"
    )

    val badMethodError = ErrorResponse(
        errorCode = 1,
        errorDetails = "Not supported method"
    )
}
