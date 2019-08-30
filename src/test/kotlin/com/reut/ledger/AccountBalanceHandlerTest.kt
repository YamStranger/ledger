package com.reut.ledger

import com.reut.ledger.core.LedgerService
import com.reut.ledger.rest.QueryParam
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.handler.AccountBalanceHandler
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
class AccountBalanceHandlerTest {
    private val ledgerService: LedgerService = mockk()
    private val handler = AccountBalanceHandler(ledgerService)

    @BeforeEach
    fun clear() {
        clearMocks(ledgerService)
    }

    @Test
    fun `executes ledger service if request correct`() {
        every {
            ledgerService.getAccountBalance(any())
        } returns TestResponses.accountBalance.body

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
    fun `throws exception if account balance can't be found`() {
        every {
            ledgerService.getAccountBalance(any())
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
