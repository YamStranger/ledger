package com.reut.ledger.rest.handler

import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.ErrorObject
import com.reut.ledger.rest.response.HandlerResponse
import com.reut.ledger.rest.response.ResponsesFactory
import mu.KotlinLogging

class CantFindHandler : LedgerHandler<Unit, ErrorObject> {
    private val logger = KotlinLogging.logger {}

    override fun handleRequest(request: Request<Unit>): HandlerResponse<ErrorObject> {
        return ResponsesFactory.getNotFoundError().also {
            logger.debug { "Can't find path ${request.path}, errorId = ${it.requestId}" }
        }
    }

    // FIXME find better way to deserialize body
    override fun getBodyClass(): Class<Unit> = Unit.javaClass
}
