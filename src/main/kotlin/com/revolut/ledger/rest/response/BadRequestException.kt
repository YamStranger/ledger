package com.revolut.ledger.rest.response

class BadRequestException(
    val errorResponse: HandlerResponse<ErrorObject>
) : Exception()
