package com.reut.ledger

import com.reut.ledger.core.LedgerService
import com.reut.ledger.model.QueryParam
import com.reut.ledger.rest.handler.AccountTransactionsHandler
import com.reut.ledger.rest.request.Request
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.util.TestResponses
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.undertow.util.StatusCodes
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertThrows

@TestInstance(PER_CLASS)
class AccountTransactionsHandlerTest {
    private val ledgerService: LedgerService = mockk()
    private val handler = AccountTransactionsHandler(ledgerService)

    @BeforeEach
    fun clear() {
        clearMocks(ledgerService)
    }

    @Test
    fun `calls ledger service if request correct`() {
        every {
            ledgerService.listTransactions(any())
        } returns TestResponses.accountTransactions.body.transactions

        val response = handler.handleRequest(Request(
            requestId = UUID.randomUUID(),
            path = "",
            body = null,
            queryParams = mapOf(QueryParam.ACCOUNT_ID to UUID.randomUUID().toString())
        ))

        assertEquals(StatusCodes.OK, response.statusCode)
        assertEquals(TestResponses.accountBalance, TestResponses.accountBalance)
    }

    @Test
    fun `throws exception if account can't be found`() {
        every {
            ledgerService.listTransactions(any())
        } returns null

        assertThrows<BadRequestException> {
            handler.handleRequest(Request(
                requestId = UUID.randomUUID(),
                path = "",
                body = null,
                queryParams = mapOf(QueryParam.ACCOUNT_ID to UUID.randomUUID().toString())
            ))
        }
    }

    @Test
    fun `throws exception if account id not provided`() {
        every {
            ledgerService.getAccountBalance(any())
        } returns null

        assertThrows<BadRequestException> {
            handler.handleRequest(Request(
                requestId = UUID.randomUUID(),
                path = "",
                body = null,
                queryParams = emptyMap()
            ))
        }
    }
}
