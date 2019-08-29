package com.reut.ledger.rest.response

import io.undertow.util.StatusCodes
import java.util.UUID

object ResponsesFactory {
    fun getNotFoundError() = HandlerResponse(
        id = UUID.randomUUID(),
        statusCode = StatusCodes.NOT_FOUND,
        body = ErrorObject(
            errorCode = 0,
            errorDetails = "Not found"
        )
    )

    fun getBadMethodError() = HandlerResponse(
        id = UUID.randomUUID(),
        statusCode = StatusCodes.BAD_REQUEST,
        body = ErrorObject(
            errorCode = 1,
            errorDetails = "Not supported method"
        )
    )

    fun getBadPathParameterError() = HandlerResponse(
        id = UUID.randomUUID(),
        statusCode = StatusCodes.BAD_REQUEST,
        body = ErrorObject(
            errorCode = 2,
            errorDetails = "Not supported path parameter"
        )
    )
}
