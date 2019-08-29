package com.reut.ledger.util

import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.AccountTransactions
import com.reut.ledger.model.Currency
import com.reut.ledger.model.Transaction
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

    val transaction = HandlerResponse(
        id = UUID.randomUUID(),
        statusCode = StatusCodes.OK,
        body = Transaction(
            from = UUID.randomUUID(),
            to = UUID.randomUUID(),
            fromCurrency = Currency.GBP,
            toCurrency = Currency.GBP,
            amount = 1000
        )
    )

    val accountTransactions = HandlerResponse(
        id = UUID.randomUUID(),
        statusCode = StatusCodes.OK,
        body = AccountTransactions(
            transactions = listOf(
                Transaction(
                    from = UUID.randomUUID(),
                    to = UUID.randomUUID(),
                    fromCurrency = Currency.GBP,
                    toCurrency = Currency.GBP,
                    amount = 1000
                )
            )
        )
    )
}
