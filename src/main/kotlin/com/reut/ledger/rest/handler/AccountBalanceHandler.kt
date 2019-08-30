package com.reut.ledger.rest.handler

import com.reut.ledger.core.LedgerService
import com.reut.ledger.core.toUUIDorNull
import com.reut.ledger.model.AccountBalance
import com.reut.ledger.rest.QueryParam.ACCOUNT_ID
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.HandlerResponse
import com.reut.ledger.rest.response.ResponsesFactory
import io.undertow.util.StatusCodes.OK
import javax.inject.Inject

class AccountBalanceHandler @Inject constructor(
    private val ledgerService: LedgerService
) : LedgerHandler<Unit, AccountBalance> {
    override fun handleRequest(request: Request<Unit>): HandlerResponse<AccountBalance> {
        val accountId = request.queryParams[ACCOUNT_ID]?.toUUIDorNull()
            ?: throw BadRequestException(ResponsesFactory.getBadPathParameterError("$ACCOUNT_ID is not UUID"))
        return HandlerResponse(
            statusCode = OK,
            body = ledgerService.getAccountBalance(
                accountId = accountId
            ) ?: throw BadRequestException(
                ResponsesFactory.getNotFoundError()
            )
        )
    }
}
