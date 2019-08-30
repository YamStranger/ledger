package com.reut.ledger.rest.handler

import com.reut.ledger.core.LedgerService
import com.reut.ledger.model.AccountCreatedConfirmation
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.HandlerResponse
import io.undertow.util.StatusCodes.OK
import javax.inject.Inject

class CreateAccountHandler @Inject constructor(
    private val ledgerService: LedgerService
) : LedgerHandler<Unit, AccountCreatedConfirmation> {
    override fun handleRequest(request: Request<Unit>): HandlerResponse<AccountCreatedConfirmation> {
        return HandlerResponse(
            statusCode = OK,
            body = AccountCreatedConfirmation(
                accountId = ledgerService.createAccount()
            )
        )
    }

    // FIXME find better way to describe body type
    override fun getBodyClass() = Unit::class.java
}
