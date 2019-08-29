package com.reut.ledger.util

import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.Currency
import com.reut.ledger.rest.response.ErrorObject
import com.reut.ledger.rest.response.HandlerResponse
import io.undertow.util.StatusCodes
import java.util.UUID

object TestResponses {
    val notFoundError = HandlerResponse(
        id = UUID.randomUUID(),
        statusCode = StatusCodes.NOT_FOUND,
        body = ErrorObject(
            errorCode = 0,
            errorDetails = "Some error details"
        )
    )

    val accountBalance = HandlerResponse(
        id = UUID.randomUUID(),
        statusCode = StatusCodes.OK,
        body = AccountBalance(
            accountId = UUID.randomUUID(),
            balance = 1000,
            currency = Currency.GBP
        )
    )
}
