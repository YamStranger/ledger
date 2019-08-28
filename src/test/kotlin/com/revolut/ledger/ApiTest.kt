package com.revolut.ledger

import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.revolut.ledger.config.AppConfiguration
import com.revolut.ledger.config.HttpServerConfiguration
import com.revolut.ledger.model.Transaction
import com.revolut.ledger.rest.Request
import com.revolut.ledger.rest.handler.CantFindHandler
import com.revolut.ledger.rest.handler.OkHttpHandler
import com.revolut.ledger.rest.response.ErrorObject
import com.revolut.ledger.rest.response.HandlerResponse
import com.revolut.ledger.rest.response.HttpResponse
import com.typesafe.config.ConfigFactory
import io.mockk.mockk
import io.undertow.util.StatusCodes
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.getInstance
import io.mockk.every
import io.mockk.verify
import java.util.UUID

/**
 * Should be integration test, but for simplicity created as Unit test
 */
@TestInstance(PER_CLASS)
class ApiTest {
    private var injector: Injector = mockk()
    private var application: Application = mockk()
    private var testModule: TestModule = mockk()

    @BeforeAll
    fun init() {
        val config = loadConfig()
        testModule = TestModule()
        injector = startApp(config, listOf(testModule))
        application = injector.getInstance()

        every {
            testModule.cantFindHandler.handleRequest(any<Request>())
        } returns TestResponses.notFoundError
    }

    @AfterAll
    fun stop() {
        application.stop()
    }

    @Test
    fun `responses with not found error for unknown path`() {
        val (_, fuelResponse, result) = "http://127.0.0.1:8081/my/random/path"
            .httpGet()
            .responseObject<HttpResponse<ErrorObject>>()
        assertEquals(StatusCodes.NOT_FOUND, fuelResponse.statusCode)

        val (response, error) = result.component1() to result.component2()
        assertNull(error)
        assertNotNull(response)
        verify(exactly = 1) {
            testModule.cantFindHandler.handleRequest(match<Request> {
                it == Request(
                    path = "/my/random/path"
                )
            })
        }
    }

    private class TestModule : KotlinModule() {
        val cantFindHandler: CantFindHandler = mockk()
        val okHttpHandler: OkHttpHandler = mockk()

        override fun configure() {
            bind<CantFindHandler>().toInstance(cantFindHandler)
            bind<OkHttpHandler>().toInstance(okHttpHandler)
        }
    }
}

object TestResponses {
    val notFoundError = HandlerResponse(
        id = UUID.randomUUID(),
        statusCode = StatusCodes.OK,
        body = ErrorObject(
            errorCode = 0,
            errorDetails = "Some error details"
        )
    )
}
