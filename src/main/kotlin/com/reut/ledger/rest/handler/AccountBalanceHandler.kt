package com.reut.ledger.rest.handler

import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.Currency
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.HandlerResponse
import io.undertow.util.StatusCodes
import java.util.UUID

class AccountBalanceHandler : LedgerHandler<AccountBalance> {
    override fun handleRequest(request: Request) =
        HandlerResponse(
            statusCode = StatusCodes.OK,
            body = AccountBalance(
                accountId = UUID.randomUUID(),
                balance = 1000,
                currency = Currency.GBP
            )
        )
}
