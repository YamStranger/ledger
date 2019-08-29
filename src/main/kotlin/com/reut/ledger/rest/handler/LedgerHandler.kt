package com.reut.ledger.rest.handler

import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.HandlerResponse

interface LedgerHandler<T> {
    fun handleRequest(request: Request): HandlerResponse<T>
}
