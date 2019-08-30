package com.reut.ledger.rest.response

import io.undertow.util.StatusCodes

object ResponsesFactory {
    fun getNotFoundError() = HandlerResponse(
        statusCode = StatusCodes.NOT_FOUND,
        body = ErrorObject(
            errorCode = 0,
            errorDetails = "Not found"
        )
    )

    fun getRequestError() = HandlerResponse(
        statusCode = StatusCodes.BAD_REQUEST,
        body = ErrorObject(
            errorCode = 1,
            errorDetails = "Bad request"
        )
    )

    fun getCantExecuteOperationError(message: String? = null) = HandlerResponse(
        statusCode = StatusCodes.BAD_REQUEST,
        body = ErrorObject(
            errorCode = 2,
            errorDetails = message ?: "Bad request"
        )
    )

    fun getEmptyBodyError() = HandlerResponse(
        statusCode = StatusCodes.BAD_REQUEST,
        body = ErrorObject(
            errorCode = 3,
            errorDetails = "Empty body"
        )
    )

    fun getBadPathParameterError(message: String? = null) = HandlerResponse(
        statusCode = StatusCodes.BAD_REQUEST,
        body = ErrorObject(
            errorCode = 4,
            errorDetails = message ?: "Not supported path parameter"
        )
    )

    fun getInternalServerError(message: String? = null) = HandlerResponse(
        statusCode = StatusCodes.INTERNAL_SERVER_ERROR,
        body = ErrorObject(
            errorCode = 4,
            errorDetails = message ?: "Internal server error"
        )
    )
}
