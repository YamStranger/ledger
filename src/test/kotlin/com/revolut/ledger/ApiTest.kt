package com.revolut.ledger

import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.google.inject.Injector
import com.revolut.ledger.rest.response.ErrorObject
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

/**
 * Should be integration test, but for simplicity created as Unit test
 */
@TestInstance(PER_CLASS)
class ApiTest {
    var injector: Injector = mockk()
    var application: Application = mockk()

    @BeforeAll
    fun init() {
        val config = ConfigFactory.empty()
        injector = startApp(config)
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
    }
}
