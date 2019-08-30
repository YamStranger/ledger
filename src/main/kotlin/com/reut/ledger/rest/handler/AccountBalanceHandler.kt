package com.reut.ledger.rest.handler

import com.reut.ledger.core.LedgerService
import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.Currency
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.HandlerResponse
import com.reut.ledger.rest.response.ResponsesFactory
import io.undertow.util.StatusCodes
import java.util.UUID
import javax.inject.Inject

class AccountBalanceHandler @Inject constructor(
    private val ledgerService: LedgerService
) : LedgerHandler<Unit, AccountBalance> {
    override fun handleRequest(request: Request<Unit>) =
        HandlerResponse(
            statusCode = StatusCodes.OK,
            body = ledgerService.getAccountBalance(
                accountId = UUID.randomUUID(),
                currency = Currency.GBP
            ) ?: throw BadRequestException(
                ResponsesFactory.getNotFoundError()
            )
        )

    // FIXME find better way to describe body type
    override fun getBodyClass(): Class<Unit> = Unit.javaClass
}
