package com.revolut.ledger.rest

class BadRequestException(
    val errorResponse: HandlerResponse<ErrorObject>
) : Exception()
