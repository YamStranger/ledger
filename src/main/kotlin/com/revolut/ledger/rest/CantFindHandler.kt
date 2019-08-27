package com.revolut.ledger.rest

import io.undertow.util.StatusCodes
import java.util.UUID
import mu.KotlinLogging

class CantFindHandler : LedgerHandler<ErrorObject>() {
    private val logger = KotlinLogging.logger {}

    override fun handleRequest(request: Request): HandlerResponse<ErrorObject> {
        val errorId = UUID.randomUUID()
        return HandlerResponse(
            id = errorId,
            statusCode = StatusCodes.NOT_FOUND,
            body = Responses.notFoundError
        ).also {
            logger.debug { "Can't find path ${request.path}" }
        }
    }
}
