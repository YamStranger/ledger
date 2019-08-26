package com.revolut.ledger.rest

import com.google.gson.Gson
import io.undertow.Handlers
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.BlockingHandler
import io.undertow.server.handlers.ExceptionHandler
import javax.inject.Inject

class HandlerFactory @Inject constructor(
    private val okHttpHandler: OkHttpHandler,
    private val cantFindHandler: CantFindHandler
) {
    companion object {
        private val gson = Gson()
    }

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
        exchange.statusCode = 400
        val exception = exchange.getAttachment(ExceptionHandler.THROWABLE) as BadRequestException
        exchange.responseSender.send(
            getJsonErrorMessage(
                exception.errorResponse
            ))
    }

    private fun handleUndefinedException(exchange: HttpServerExchange) {
        exchange.statusCode = 500
        exchange.responseSender.send(getJsonErrorMessage(ErrorResponse(
            errorCode = 0,
            errorDetails = "Internal Error")
        ))
    }

    private fun getJsonErrorMessage(message: ErrorResponse): String {
        return gson.toJson(message)
    }
}
