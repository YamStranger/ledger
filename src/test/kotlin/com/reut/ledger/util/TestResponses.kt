package com.reut.ledger.util

import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.AccountCreatedConfirmation
import com.reut.ledger.model.AccountTransactions
import com.reut.ledger.model.Currency
import com.reut.ledger.model.Transaction
import com.reut.ledger.model.TransactionConfirmation
import com.reut.ledger.rest.response.ErrorObject
import com.reut.ledger.rest.response.HandlerResponse
import io.undertow.util.StatusCodes
import java.time.Instant
import java.util.UUID

object TestResponses {
    val notFoundError = HandlerResponse(
        statusCode = StatusCodes.NOT_FOUND,
        body = ErrorObject(
            errorCode = 0,
            errorDetails = "Some error details"
        )
    )

    val accountBalance = HandlerResponse(
        statusCode = StatusCodes.OK,
        body = AccountBalance(
            accountId = UUID.randomUUID(),
            balances = mapOf(
                Currency.GBP to 1000L
            )
        )
    )

    val transactionConfirmation = HandlerResponse(
        statusCode = StatusCodes.OK,
        body = TransactionConfirmation(
            transactionId = UUID.randomUUID()
        )
    )

    val accountCreatedConfirmation = HandlerResponse(
        statusCode = StatusCodes.OK,
        body = AccountCreatedConfirmation(
            accountId = UUID.randomUUID()
        )
    )

    val transaction = HandlerResponse(
        statusCode = StatusCodes.OK,
        body = Transaction(
            id = UUID.randomUUID(),
            from = UUID.randomUUID(),
            to = UUID.randomUUID(),
            currency = Currency.GBP,
            amount = 1000,
            createdAt = Instant.now().toEpochMilli()
        )
    )

    val accountTransactions = HandlerResponse(
        statusCode = StatusCodes.OK,
        body = AccountTransactions(
            transactions = listOf(
                Transaction(
                    id = UUID.randomUUID(),
                    from = UUID.randomUUID(),
                    to = UUID.randomUUID(),
                    currency = Currency.GBP,
                    amount = 1000,
                    createdAt = Instant.now().toEpochMilli()
                )
            )
        )
    )
}
