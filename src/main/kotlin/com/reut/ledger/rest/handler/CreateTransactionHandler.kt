package com.reut.ledger.rest.handler

import com.reut.ledger.core.LedgerService
import com.reut.ledger.model.CreateTransactionRequest
import com.reut.ledger.model.Currency
import com.reut.ledger.model.TransactionConfirmation
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.HandlerResponse
import com.reut.ledger.rest.response.ResponsesFactory
import io.undertow.util.StatusCodes
import java.util.UUID
import javax.inject.Inject

class CreateTransactionHandler @Inject constructor(
    private val ledgerService: LedgerService
) : LedgerHandler<CreateTransactionRequest, TransactionConfirmation> {
    override fun handleRequest(request: Request<CreateTransactionRequest>) =
        HandlerResponse(
            statusCode = StatusCodes.OK,
            body = TransactionConfirmation(
                transactionId = ledgerService.processTransaction(
                    from = UUID.randomUUID(),
                    to = UUID.randomUUID(),
                    currency = Currency.GBP,
                    amount = 1000
                ).let {
                    if (it.errorReason != null) {
                        throw BadRequestException(
                            ResponsesFactory.getRequestError()
                        )
                    } else {
                        it.transactionId
                            ?: throw IllegalStateException("transactionId and error reason should never be null together")
                    }
                }
            )
        )

    // FIXME find better way to describe body type
    override fun getBodyClass() = CreateTransactionRequest::class.java
}
