package com.reut.ledger

import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.inject.Injector
import com.reut.ledger.config.ConfigurationModule.Companion.SERVER_HTTP_PORT
import com.reut.ledger.config.HttpServerConfiguration
import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.AccountTransactions
import com.reut.ledger.model.Transaction
import com.reut.ledger.model.TransactionConfirmation
import com.reut.ledger.rest.JsonUtil
import com.reut.ledger.rest.QueryParam
import com.reut.ledger.rest.handler.LedgerHandler
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.ErrorObject
import com.reut.ledger.rest.response.HandlerResponse
import com.reut.ledger.rest.response.HttpResponse
import com.reut.ledger.util.RestMockModule
import com.reut.ledger.util.TestRequests
import com.reut.ledger.util.TestResponses
import com.reut.ledger.util.execute
import com.reut.ledger.util.findFreePort
import com.typesafe.config.ConfigValueFactory
import dev.misfitlabs.kotlinguice4.getInstance
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

/**
 * This test created to validate that rest requests processed correctly and passed to handlers correctly.
 * It mocks handlers, and tests only rest server implementation, to make sure that params/body parsed correctly and provided to handlers.
 */
@TestInstance(PER_CLASS)
class RestServiceApiTest {
    private var injector: Injector = mockk()
    private var application: Application = mockk()
    private var testModule: RestMockModule = RestMockModule()
    private var serverConfiguration: HttpServerConfiguration = mockk()
    private var rootPath: String = ""
    private val accountId = UUID.randomUUID()
    private val transactionId = UUID.randomUUID()

    init {
        val config = loadConfig().withValue(
            SERVER_HTTP_PORT, ConfigValueFactory.fromAnyRef(findFreePort())
        )

        injector = config.createInjector(listOf(testModule))
        application = injector.startApp()
        serverConfiguration = injector.getInstance()
        rootPath = "http://${serverConfiguration.host}:${serverConfiguration.port}"
    }

    @BeforeEach
    fun clearMocks() {
        testModule.clearMocks()
    }

    @AfterAll
    fun stop() {
        application.stop()
        clearAllMocks()
    }

    /**
     * Test for register(routingHandler, Methods.GET, "*", handlerFactory.getCantFindHandler())
     */
    @Test
    fun `responses with not found error for unknown path`() {
        every {
            testModule.cantFindHandler.handleRequest(any())
        } returns TestResponses.notFoundError
        val randomPath = "/my/random/path"
        val (fuelResponse, response, _) = "$rootPath$randomPath"
            .httpGet()
            .execute<HttpResponse<ErrorObject>>()

        assertNotNull(response)

        verify(exactly = 1) {
            testModule.cantFindHandler.handleRequest(match {
                it.path == randomPath &&
                    it.queryParams.isEmpty()
            })
        }

        assertEquals(TestResponses.notFoundError.statusCode, fuelResponse.statusCode)
        assertEquals(TestResponses.notFoundError.body, response!!.body)
    }

    @Test
    fun `returns error if handler returns error`() {
        every {
            testModule.accountBalanceHandler.handleRequest(match {
                accountId.toString() == it.queryParams[QueryParam.ACCOUNT_ID]
            })
        } throws BadRequestException(
            TestResponses.notFoundError
        )

        val accountBalancePath = "/account/$accountId/balance"
        val (fuelResponse, response, _) = "$rootPath$accountBalancePath"
            .httpGet()
            .execute<HttpResponse<ErrorObject>>()

        validateResponse(
            mockedHandler = testModule.accountBalanceHandler,
            fuelResponse = fuelResponse,
            response = response,
            mockedResponse = TestResponses.notFoundError,
            queryParam = QueryParam.ACCOUNT_ID,
            queryParamValue = accountId.toString(),
            path = accountBalancePath
        )
    }

    /**
     * Test for register(routingHandler, Methods.GET, "/account/{$ACCOUNT_ID}/balance", handlerFactory.getAccountBalanceHandler())
     */
    @Test
    fun `returns account balance`() {
        every {
            testModule.accountBalanceHandler.handleRequest(match {
                accountId.toString() == it.queryParams[QueryParam.ACCOUNT_ID]
            })
        } returns TestResponses.accountBalance
        val accountBalancePath = "/account/$accountId/balance"
        val (fuelResponse, response, _) = "$rootPath$accountBalancePath"
            .httpGet()
            .execute<HttpResponse<AccountBalance>>()

        validateResponse(
            mockedHandler = testModule.accountBalanceHandler,
            fuelResponse = fuelResponse,
            mockedResponse = TestResponses.accountBalance,
            response = response,
            queryParam = QueryParam.ACCOUNT_ID,
            queryParamValue = accountId.toString(),
            path = accountBalancePath
        )
    }

    /**
     * Test for register(routingHandler, Methods.GET, "/account/{$ACCOUNT_ID}/transactions", handlerFactory.getAccountTransactionsHandler())
     */
    @Test
    fun `returns account transactions`() {
        every {
            testModule.accountTransactionsHandler.handleRequest(match {
                accountId.toString() == it.queryParams[QueryParam.ACCOUNT_ID]
            })
        } returns TestResponses.accountTransactions
        val accountTransactionsPath = "/account/$accountId/transactions"
        val (fuelResponse, response, _) = "$rootPath$accountTransactionsPath"
            .httpGet()
            .execute<HttpResponse<AccountTransactions>>()

        validateResponse(
            mockedHandler = testModule.accountTransactionsHandler,
            fuelResponse = fuelResponse,
            response = response,
            mockedResponse = TestResponses.accountTransactions,
            queryParam = QueryParam.ACCOUNT_ID,
            queryParamValue = accountId.toString(),
            path = accountTransactionsPath
        )
    }

    /**
     * Test for register(routingHandler, Methods.POST, "/transaction", handlerFactory.postTransactionHandler())
     */
    @Test
    fun `accepts transaction request`() {
        every {
            testModule.createTransactionHandler.handleRequest(any())
        } returns TestResponses.transactionConfirmation
        val createTransactionPath = "/transaction"
        val (fuelResponse, response, _) = "$rootPath$createTransactionPath"
            .httpPost()
            .body(JsonUtil.serialize(TestRequests.transaction))
            .execute<HttpResponse<TransactionConfirmation>>()

        assertNotNull(response)

        verify(exactly = 1) {
            testModule.createTransactionHandler.handleRequest(match {
                it.path == createTransactionPath &&
                    it.queryParams.isEmpty()
            })
        }

        assertEquals(TestResponses.transactionConfirmation.statusCode, fuelResponse.statusCode)
        assertEquals(TestResponses.transactionConfirmation.body, response!!.body)
    }

    /**
     * Test for register(routingHandler, Methods.GET, "/transaction/{$TRANSACTION_ID}", handlerFactory.getTransactionHandler())
     */
    @Test
    fun `returns transaction`() {
        every {
            testModule.transactionHandler.handleRequest(match {
                transactionId.toString() == it.queryParams[QueryParam.TRANSACTION_ID]
            })
        } returns TestResponses.transaction
        val transactionPath = "/transaction/$transactionId"
        val (fuelResponse, response, _) = "$rootPath$transactionPath"
            .httpGet()
            .execute<HttpResponse<Transaction>>()

        validateResponse(
            mockedHandler = testModule.transactionHandler,
            fuelResponse = fuelResponse,
            response = response,
            mockedResponse = TestResponses.transaction,
            queryParam = QueryParam.TRANSACTION_ID,
            queryParamValue = transactionId.toString(),
            path = transactionPath
        )
    }

    private fun <M, T> validateResponse(
        fuelResponse: Response,
        response: HttpResponse<*>?,
        mockedResponse: HandlerResponse<*>,
        mockedHandler: LedgerHandler<M, T>,
        queryParam: QueryParam,
        queryParamValue: String,
        path: String
    ) {
        assertNotNull(response)
        verify(exactly = 1) {
            mockedHandler.handleRequest(match {
                it.path == path &&
                    it.queryParams[queryParam] == queryParamValue
            })
        }

        assertEquals(fuelResponse.statusCode, fuelResponse.statusCode)
        assertEquals(mockedResponse.body, response!!.body)
    }
}
