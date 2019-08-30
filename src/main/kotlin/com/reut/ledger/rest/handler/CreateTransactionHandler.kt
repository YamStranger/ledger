package com.reut.ledger.rest.handler

import com.reut.ledger.model.Transaction
import com.reut.ledger.model.TransactionConfirmation
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.HandlerResponse
import io.undertow.util.StatusCodes
import java.util.UUID

class CreateTransactionHandler : LedgerHandler<Transaction, TransactionConfirmation> {
    override fun handleRequest(request: Request<Transaction>) =
        HandlerResponse(
            statusCode = StatusCodes.OK,
            body = TransactionConfirmation(
                transactionId = UUID.randomUUID()
            )
        )

    // FIXME find better way to deserialize body
    override fun getBodyClass() = Transaction::class.java
}
