package com.reut.ledger.rest.handler

import com.reut.ledger.model.Transaction
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.HandlerResponse
import io.undertow.util.StatusCodes
import java.util.UUID

class OkHttpHandler : LedgerHandler<Transaction> {
    override fun handleRequest(request: Request) =
        HandlerResponse(
            statusCode = StatusCodes.OK,
            body = Transaction(UUID.randomUUID())
        )
}
