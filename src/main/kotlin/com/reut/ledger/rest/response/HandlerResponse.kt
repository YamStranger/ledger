package com.reut.ledger.rest.response

data class HandlerResponse<T>(
    val statusCode: Int,
    val body: T
)
