package com.reut.ledger.rest.handler

import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.ErrorObject
import com.reut.ledger.rest.response.HandlerResponse
import com.reut.ledger.rest.response.ResponsesFactory

class CantFindHandler : LedgerHandler<Unit, ErrorObject> {
    override fun handleRequest(request: Request<Unit>): HandlerResponse<ErrorObject> {
        return ResponsesFactory.getNotFoundError()
    }

    // FIXME find better way to describe body type
    override fun getBodyClass(): Class<Unit> = Unit.javaClass
}
