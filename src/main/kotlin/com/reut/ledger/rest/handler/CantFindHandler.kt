package com.reut.ledger.rest.handler

import com.reut.ledger.rest.request.Request
import com.reut.ledger.rest.response.ErrorObject
import com.reut.ledger.rest.response.HandlerResponse
import com.reut.ledger.rest.response.ResponsesFactory

class CantFindHandler : LedgerHandler<Unit, ErrorObject> {
    override fun handleRequest(request: Request<Unit>): HandlerResponse<ErrorObject> {
        return ResponsesFactory.getNotFoundError()
    }
}
