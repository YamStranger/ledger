package com.reut.ledger.rest.handler

import com.reut.ledger.rest.JsonUtil.serialize
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.HttpResponse
import com.reut.ledger.rest.response.ResponsesFactory
import io.undertow.Handlers
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.AllowedMethodsHandler
import io.undertow.server.handlers.BlockingHandler
import io.undertow.server.handlers.ExceptionHandler.THROWABLE
import io.undertow.util.Methods.GET
import io.undertow.util.Methods.OPTIONS
import io.undertow.util.Methods.POST
import io.undertow.util.StatusCodes.INTERNAL_SERVER_ERROR
import javax.inject.Inject
import mu.KotlinLogging

class HandlerFactory @Inject constructor(
    private val cantFindHandler: CantFindHandler,
    private val accountBalanceHandler: AccountBalanceHandler,
    private val postTransactionHandler: CreateTransactionHandler,
    private val transactionHandler: TransactionHandler,
    private val accountTransactionsHandler: AccountTransactionsHandler,
    private val postCreateAccountHandler: CreateAccountHandler
) {
    private val logger = KotlinLogging.logger {}

    fun getCantFindHandler() = cantFindHandler.instrumented()
    fun getAccountBalanceHandler() = accountBalanceHandler.instrumented()
    fun postTransactionHandler() = postTransactionHandler.instrumented()
    fun getTransactionHandler() = transactionHandler.instrumented()
    fun getAccountTransactionsHandler() = accountTransactionsHandler.instrumented()
    fun postCreateAccountHandler() = postCreateAccountHandler.instrumented()

    private fun <M, T> LedgerHandler<M, T>.instrumented(): HttpHandler {
        return LedgerHttpHandler(this)
            .withDefaults()
            .asBlockingHandler()
    }

    private fun HttpHandler.withExceptionHandling() =
        Handlers.exceptionHandler(this)
            .addExceptionHandler(BadRequestException::class.java) { handleBadRequestException(it) }
            .addExceptionHandler(Exception::class.java) { handleUndefinedException(it) }
            .addExceptionHandler(RuntimeException::class.java) { handleUndefinedException(it) }

    private fun HttpHandler.withDefaults(): HttpHandler =
        this.withExceptionHandling()
            .withCors()
            .withAllowedMethods()

    private fun HttpHandler.asBlockingHandler() = BlockingHandler(this)

    private fun HttpHandler.withCors() = CorsHandler(this)

    private fun HttpHandler.withAllowedMethods() = AllowedMethodsHandler(this, POST, GET, OPTIONS)

    private fun handleBadRequestException(exchange: HttpServerExchange) {
        val exception = exchange.getAttachment(THROWABLE) as BadRequestException
        val requestId = exchange.getAttachment(requestIdKey)
        logger.debug { "BadRequest{$requestId}: ${exception.errorResponse}" }
        exchange.statusCode = exception.errorResponse.statusCode
        exchange.responseSender.send(
            serialize(HttpResponse(
                requestId = requestId,
                body = exception.errorResponse.body
            ))
        )
    }

    private fun handleUndefinedException(exchange: HttpServerExchange) {
        val exception = exchange.getAttachment(THROWABLE)
        val requestId = exchange.getAttachment(requestIdKey)
        logger.error(exception) { "UndefinedException{$requestId}" }
        exchange.statusCode = INTERNAL_SERVER_ERROR
        exchange.responseSender.send(
            serialize(HttpResponse(
                requestId = requestId,
                body = ResponsesFactory.getInternalServerError().body
            ))
        )
    }
}
