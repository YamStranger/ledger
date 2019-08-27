package com.revolut.ledger.rest

import com.revolut.ledger.model.Transaction
import io.undertow.util.StatusCodes
import java.util.UUID

class OkHttpHandler : LedgerHandler<Transaction>() {
    override fun handleRequest(request: Request) =
        HandlerResponse(
            statusCode = StatusCodes.OK,
            body = Transaction(UUID.randomUUID())
        )
}
