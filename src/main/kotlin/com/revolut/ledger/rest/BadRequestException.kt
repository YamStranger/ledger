package com.revolut.ledger.rest

class BadRequestException(
    val errorResponse: ErrorResponse
) : Exception()
