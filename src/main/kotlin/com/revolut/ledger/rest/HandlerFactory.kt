package com.revolut.ledger.rest

import com.revolut.ledger.rest.ObjectMapper.serialize
import com.revolut.ledger.rest.ObjectMapper.toJson
import io.undertow.Handlers
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.BlockingHandler
import io.undertow.server.handlers.ExceptionHandler
import io.undertow.util.StatusCodes
import java.util.UUID
import javax.inject.Inject
import mu.KotlinLogging

class HandlerFactory @Inject constructor(
    private val okHttpHandler: OkHttpHandler,
    private val cantFindHandler: CantFindHandler
) {
    private val logger = KotlinLogging.logger {}

    fun getOkHandler(): HttpHandler = okHttpHandler.instrumented()
    fun getCantFindHandler(): HttpHandler = cantFindHandler.instrumented()

    private fun HttpHandler.instrumented(): HttpHandler =
        this.asBlockingHandler()
            .withDefaults()

    private fun HttpHandler.withExceptionHandling() =
        Handlers.exceptionHandler(this)
            .addExceptionHandler(BadRequestException::class.java) { handleBadRequestException(it) }
            .addExceptionHandler(Exception::class.java) { handleUndefinedException(it) }
            .addExceptionHandler(RuntimeException::class.java) { handleUndefinedException(it) }

    private fun HttpHandler.withDefaults(): HttpHandler =
        this.withExceptionHandling()
            .withCors()

    private fun HttpHandler.asBlockingHandler() = BlockingHandler(this)

    private fun HttpHandler.withCors() = CorsHandler(this)

    private fun handleBadRequestException(exchange: HttpServerExchange) {

        val exception = exchange.getAttachment(ExceptionHandler.THROWABLE) as BadRequestException
        exchange.statusCode = exception.errorResponse.statusCode
        exchange.responseSender.send(
            serialize(toJson(HttpResponse(
                id = exception.errorResponse.id,
                body = exception.errorResponse.body
            )))
        )
    }

    private fun handleUndefinedException(exchange: HttpServerExchange) {
        exchange.statusCode = StatusCodes.INTERNAL_SERVER_ERROR
        val exception = exchange.getAttachment(ExceptionHandler.THROWABLE)
        val errorId = UUID.randomUUID()
        logger.error(exception) { "undefinedException: $errorId" }
        exchange.responseSender.send(
            serialize(toJson(HttpResponse(
                id = errorId,
                body = ErrorObject(
                    errorCode = 0,
                    errorDetails = "Internal Error")
            )))
        )
    }
}
