package com.reut.ledger.rest.handler

import com.reut.ledger.rest.JsonUtil.serialize
import com.reut.ledger.rest.JsonUtil.toJson
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.ErrorObject
import com.reut.ledger.rest.response.HttpResponse
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
    private val cantFindHandler: CantFindHandler,
    private val accountBalanceHandler: AccountBalanceHandler
) {
    private val logger = KotlinLogging.logger {}

    fun getOkHandler() = okHttpHandler.instrumented()
    fun getCantFindHandler() = cantFindHandler.instrumented()
    fun getAccountBalanceHandler() = accountBalanceHandler.instrumented()

    private fun <T> LedgerHandler<T>.instrumented(): HttpHandler {
        return BlockingHandler(Handlers.exceptionHandler(LedgerHttpHandler(this))
            .addExceptionHandler(BadRequestException::class.java) { handleBadRequestException(it) }
            .addExceptionHandler(Throwable::class.java) { handleUndefinedException(it) }
            .addExceptionHandler(RuntimeException::class.java) { handleUndefinedException(it) })
    }

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
