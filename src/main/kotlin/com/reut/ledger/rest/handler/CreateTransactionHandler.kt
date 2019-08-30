package com.reut.ledger.rest.handler

import com.reut.ledger.core.LedgerService
import com.reut.ledger.model.CreateTransactionRequest
import com.reut.ledger.model.ErrorReason
import com.reut.ledger.model.TransactionConfirmation
import com.reut.ledger.rest.request.Request
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.HandlerResponse
import com.reut.ledger.rest.response.ResponsesFactory
import io.undertow.util.StatusCodes.OK
import javax.inject.Inject

class CreateTransactionHandler @Inject constructor(
    private val ledgerService: LedgerService
) : LedgerHandler<CreateTransactionRequest, TransactionConfirmation> {
    override fun handleRequest(request: Request<CreateTransactionRequest>): HandlerResponse<TransactionConfirmation> {
        val body = request.body ?: throw BadRequestException(
            ResponsesFactory.getEmptyBodyError()
        )
        val transactionConfirmation = ledgerService.processTransaction(
            from = body.from,
            to = body.to,
            currency = body.currency,
            amount = body.amount
        )
        val transactionId = with(transactionConfirmation) {
            if (errorReason != null) {
                when (errorReason) {
                    ErrorReason.NOT_ENOUGH_BALANCE_TO_EXECUTE_TRANSACTION,
                    ErrorReason.CANT_DEPOSIT_AMOUNT_BELOW_ZERO,
                    ErrorReason.ACCOUNTS_FOR_TRANSACTION_SHOULD_BE_DIFFERENT,
                    ErrorReason.CANT_CREDIT_EXPENSES_ACCOUNT,
                    ErrorReason.CANT_DEPOSIT_TO_INCOME_ACCOUNT,
                    ErrorReason.UNSUPPORTED_TRANSACTION -> throw BadRequestException(
                        ResponsesFactory.getCantExecuteOperationError(errorReason.name)
                    )
                    ErrorReason.ACCOUNT_DOES_NO_EXISTS -> throw BadRequestException(
                        ResponsesFactory.getNotFoundError()
                    )
                    else -> throw BadRequestException(
                        ResponsesFactory.getRequestError()
                    )
                }
            } else {
                transactionId
                    ?: throw IllegalStateException("transactionId and error reason should never be null together")
            }
        }

        return HandlerResponse(
            statusCode = OK,
            body = TransactionConfirmation(
                transactionId = transactionId
            )
        )
    }
}
