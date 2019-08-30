package com.reut.ledger.util

import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.AccountTransactions
import com.reut.ledger.model.Currency
import com.reut.ledger.model.Transaction
import com.reut.ledger.model.TransactionConfirmation
import com.reut.ledger.rest.response.ErrorObject
import com.reut.ledger.rest.response.HandlerResponse
import io.undertow.util.StatusCodes
import java.util.UUID

object TestResponses {
    val notFoundError = HandlerResponse(
        requestId = UUID.randomUUID(),
        statusCode = StatusCodes.NOT_FOUND,
        body = ErrorObject(
            errorCode = 0,
            errorDetails = "Some error details"
        )
    )

    val accountBalance = HandlerResponse(
        requestId = UUID.randomUUID(),
        statusCode = StatusCodes.OK,
        body = AccountBalance(
            accountId = UUID.randomUUID(),
            balance = 1000,
            currency = Currency.GBP
        )
    )

    val transactionConfirmation = HandlerResponse(
        requestId = UUID.randomUUID(),
        statusCode = StatusCodes.OK,
        body = TransactionConfirmation(
            transactionId = UUID.randomUUID()
        )
    )

    val transaction = HandlerResponse(
        requestId = UUID.randomUUID(),
        statusCode = StatusCodes.OK,
        body = Transaction(
            id = UUID.randomUUID(),
            from = UUID.randomUUID(),
            to = UUID.randomUUID(),
            fromCurrency = Currency.GBP,
            toCurrency = Currency.GBP,
            amount = 1000
        )
    )

    val accountTransactions = HandlerResponse(
        requestId = UUID.randomUUID(),
        statusCode = StatusCodes.OK,
        body = AccountTransactions(
            transactions = listOf(
                Transaction(
                    id = UUID.randomUUID(),
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
