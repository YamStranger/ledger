package com.revolut.ledger.rest.handler

import com.revolut.ledger.rest.response.ErrorObject
import com.revolut.ledger.rest.response.HandlerResponse
import com.revolut.ledger.rest.Request
import com.revolut.ledger.rest.response.Responses
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
