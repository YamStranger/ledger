package com.reut.ledger.rest.handler

import com.reut.ledger.rest.request.Request
import com.reut.ledger.rest.response.HandlerResponse

interface LedgerHandler<M, T> {
    fun handleRequest(request: Request<M>): HandlerResponse<T>
}
