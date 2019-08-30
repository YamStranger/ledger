package com.reut.ledger.rest.handler

import com.reut.ledger.core.LedgerService
import com.reut.ledger.core.toUUIDorNull
import com.reut.ledger.model.AccountTransactions
import com.reut.ledger.rest.QueryParam.ACCOUNT_ID
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.HandlerResponse
import com.reut.ledger.rest.response.ResponsesFactory
import io.undertow.util.StatusCodes.OK
import javax.inject.Inject

class AccountTransactionsHandler @Inject constructor(
    private val ledgerService: LedgerService
) : LedgerHandler<Unit, AccountTransactions> {
    override fun handleRequest(request: Request<Unit>): HandlerResponse<AccountTransactions> {
        val accountId = request.queryParams[ACCOUNT_ID]?.toUUIDorNull()
            ?: throw BadRequestException(ResponsesFactory.getBadPathParameterError("$ACCOUNT_ID is not UUID"))
        return HandlerResponse(
            statusCode = OK,
            body = AccountTransactions(
                transactions = ledgerService.listTransactions(
                    accountId = accountId
                ) ?: throw BadRequestException(
                    ResponsesFactory.getNotFoundError()
                )
            )
        )
    }
}
