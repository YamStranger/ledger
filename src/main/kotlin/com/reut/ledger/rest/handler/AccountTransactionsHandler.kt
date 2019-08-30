package com.reut.ledger.rest.handler

import com.reut.ledger.core.LedgerService
import com.reut.ledger.model.AccountTransactions
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.HandlerResponse
import com.reut.ledger.rest.response.ResponsesFactory
import io.undertow.util.StatusCodes
import java.util.UUID
import javax.inject.Inject

class AccountTransactionsHandler @Inject constructor(
    private val ledgerService: LedgerService
) : LedgerHandler<Unit, AccountTransactions> {
    override fun handleRequest(request: Request<Unit>) =
        HandlerResponse(
            statusCode = StatusCodes.OK,
            body = AccountTransactions(
                transactions = ledgerService.listTransactions(
                    accountId = UUID.randomUUID()
                ) ?: throw BadRequestException(
                    ResponsesFactory.getNotFoundError()
                )
            )
        )

    // FIXME find better way to describe body type
    override fun getBodyClass(): Class<Unit> = Unit.javaClass
}
