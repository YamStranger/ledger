package com.reut.ledger

import com.github.kittinunf.fuel.httpGet
import com.google.inject.Injector
import com.reut.ledger.config.ConfigurationModule.Companion.SERVER_HTTP_PORT
import com.reut.ledger.config.HttpServerConfiguration
import com.reut.ledger.model.AccountBalance
import com.reut.ledger.rest.QueryParams
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.ErrorObject
import com.reut.ledger.rest.response.HttpResponse
import com.reut.ledger.util.RestMockModule
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
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

/**
 * This test created to validate that rest requests processed correctly and passed to handlers correctly.
 * It mocks handlers, and tests only rest server implementation.
 */
@TestInstance(PER_CLASS)
class RestServiceApiTest {
    private var injector: Injector = mockk()
    private var application: Application = mockk()
    private var testModule: RestMockModule = RestMockModule()
    private var serverConfiguration: HttpServerConfiguration = mockk()
    private var rootPath: String = ""
    private val accountA = UUID.randomUUID()

    init {
        val config = loadConfig().withValue(
            SERVER_HTTP_PORT, ConfigValueFactory.fromAnyRef(findFreePort())
        )

        injector = createInjector(config, listOf(testModule))
        application = startApp(injector)
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
                accountA.toString().equals(it.queryParams[QueryParams.ACCOUNT_ID])
            })
        } throws BadRequestException(
            TestResponses.notFoundError
        )

        val accountBalancePath = "/account/$accountA/balance"
        val (fuelResponse, response, _) = "$rootPath$accountBalancePath"
            .httpGet()
            .execute<HttpResponse<ErrorObject>>()

        assertNotNull(response)
        verify(exactly = 1) {
            testModule.accountBalanceHandler.handleRequest(match {
                it.path == accountBalancePath &&
                    !it.queryParams[QueryParams.ACCOUNT_ID].isNullOrEmpty()
            })
        }

        assertEquals(TestResponses.notFoundError.statusCode, fuelResponse.statusCode)
        assertEquals(TestResponses.notFoundError.body, response!!.body)
    }

    /**
     * Test for register(routingHandler, Methods.GET, "/account/{$ACCOUNT_ID}/balance", handlerFactory.getAccountBalanceHandler())
     */
    @Test
    fun `returns account balance`() {
        every {
            testModule.accountBalanceHandler.handleRequest(match {
                accountA.toString().equals(it.queryParams[QueryParams.ACCOUNT_ID])
            })
        } returns TestResponses.accountBalance
        val accountBalancePath = "/account/$accountA/balance"
        val (fuelResponse, response, _) = "$rootPath$accountBalancePath"
            .httpGet()
            .execute<HttpResponse<AccountBalance>>()

        assertNotNull(response)
        verify(exactly = 1) {
            testModule.accountBalanceHandler.handleRequest(match {
                it.path == accountBalancePath &&
                    it.queryParams[QueryParams.ACCOUNT_ID] == accountA.toString()
            })
        }

        assertEquals(TestResponses.accountBalance.statusCode, fuelResponse.statusCode)
        assertEquals(TestResponses.accountBalance.body, response!!.body)
    }

    /**
     * Test for register(routingHandler, Methods.GET, "/account/{$ACCOUNT_ID}/transactions", handlerFactory.getAccountTransactionsHandler())
     */
    @Test
    fun `returns account transactions`() {
        fail<Unit>("")
    }

    /**
     * Test for register(routingHandler, Methods.POST, "/transaction", handlerFactory.postTransactionHandler())
     */
    @Test
    fun `accepts transaction request`() {
        fail<Unit>("")
    }

    /**
     * Test for     register(routingHandler, Methods.GET, "/transaction/{$TRANSACTION_ID}", handlerFactory.getTransactionHandler())
     */
    @Test
    fun `returns transaction`() {
        fail<Unit>("")
    }
}
