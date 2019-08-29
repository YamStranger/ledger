package com.reut.ledger.rest.handler

import com.reut.ledger.model.Currency
import com.reut.ledger.model.Transaction
import com.reut.ledger.model.AccountTransactions
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.HandlerResponse
import io.undertow.util.StatusCodes
import java.util.UUID

class AccountTransactionsHandler : LedgerHandler<AccountTransactions> {
    override fun handleRequest(request: Request) =
        HandlerResponse(
            statusCode = StatusCodes.OK,
            body = AccountTransactions(
                transactions = listOf(Transaction(
                    from = UUID.randomUUID(),
                    to = UUID.randomUUID(),
                    fromCurrency = Currency.GBP,
                    toCurrency = Currency.GBP,
                    amount = 1000
                ))
            )
        )
}
