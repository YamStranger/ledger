package com.revolut.ledger.rest.response

data class ErrorObject(
    val errorCode: Int,
    val errorDetails: String
)
