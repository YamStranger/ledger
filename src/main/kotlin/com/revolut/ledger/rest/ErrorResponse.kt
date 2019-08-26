package com.revolut.ledger.rest

data class ErrorResponse(
    val errorCode: Int,
    val errorDetails: String
)
