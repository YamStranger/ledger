package com.reut.ledger

import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.google.inject.Injector
import com.reut.ledger.config.ConfigurationModule.Companion.SERVER_HTTP_PORT
import com.reut.ledger.config.HttpServerConfiguration
import com.reut.ledger.model.AccountBalance
import com.reut.ledger.rest.QueryParams
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.ErrorObject
import com.reut.ledger.rest.response.HttpResponse
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
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class ApiTest {
    private var injector: Injector = mockk()
    private var application: Application = mockk()
    private var testModule: RestMockModule = RestMockModule()
    private var serverConfiguration: HttpServerConfiguration = mockk()
    private var rootPath: String = ""
    private val accountA = UUID.randomUUID()

    @BeforeAll
    fun init() {
        val config = loadConfig().withValue(
            SERVER_HTTP_PORT, ConfigValueFactory.fromAnyRef(findFreePort())
        )

        injector = createInjector(config, listOf(testModule))
        application = startApp(injector)
        serverConfiguration = injector.getInstance()
        rootPath = "http://${serverConfiguration.host}:${serverConfiguration.port}"

        every {
            testModule.cantFindHandler.handleRequest(any())
        } returns TestResponses.notFoundError

        every {
            testModule.accountBalanceHandler.handleRequest(match {
                accountA.toString().equals(it.queryParams[QueryParams.ACCOUNT_ID])
            })
        } returns TestResponses.accountBalance

        every {
            testModule.accountBalanceHandler.handleRequest(match {
                !accountA.toString().equals(it.queryParams[QueryParams.ACCOUNT_ID])
            })
        } throws BadRequestException(
            TestResponses.notFoundError
        )
    }

    @AfterAll
    fun stop() {
        application.stop()
        clearAllMocks()
    }

    @Test
    fun `responses with not found error for unknown path`() {
        val randomPath = "/my/random/path"
        val (fuelResponse, response, _) = "$rootPath$randomPath"
            .httpGet()
            .responseObject<HttpResponse<ErrorObject>>()
            .extractErrorIfExists()

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
    fun `returns account balance`() {
        val accountBalancePath = "/ledger/$accountA/balance"
        val (fuelResponse, response, _) = "$rootPath$accountBalancePath"
            .httpGet()
            .responseObject<HttpResponse<AccountBalance>>()
            .extractErrorIfExists()

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

    @Test
    fun `returns error if account can't be found`() {
        val accountBalancePath = "/ledger/${UUID.randomUUID()}/balance"
        val (fuelResponse, response, _) = "$rootPath$accountBalancePath"
            .httpGet()
            .responseObject<HttpResponse<ErrorObject>>()
            .extractErrorIfExists()

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
}
