package com.reut.ledger.rest.response

class BadRequestException(
    val errorResponse: HandlerResponse<ErrorObject>
) : Exception()
