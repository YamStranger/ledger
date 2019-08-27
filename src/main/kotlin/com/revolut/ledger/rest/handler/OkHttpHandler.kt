package com.revolut.ledger.rest.handler

import com.revolut.ledger.model.Transaction
import com.revolut.ledger.rest.response.HandlerResponse
import com.revolut.ledger.rest.Request
import io.undertow.util.StatusCodes
import java.util.UUID

class OkHttpHandler : LedgerHandler<Transaction>() {
    override fun handleRequest(request: Request) =
        HandlerResponse(
            statusCode = StatusCodes.OK,
            body = Transaction(UUID.randomUUID())
        )
}
