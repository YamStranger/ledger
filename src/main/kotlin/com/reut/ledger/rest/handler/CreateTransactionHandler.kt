package com.reut.ledger.rest.handler

import com.reut.ledger.model.TransactionConfirmation
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.HandlerResponse
import io.undertow.util.StatusCodes
import java.util.UUID

class CreateTransactionHandler : LedgerHandler<TransactionConfirmation> {
    override fun handleRequest(request: Request) =
        HandlerResponse(
            statusCode = StatusCodes.OK,
            body = TransactionConfirmation(
                transactionId = UUID.randomUUID()
            )
        )
}
