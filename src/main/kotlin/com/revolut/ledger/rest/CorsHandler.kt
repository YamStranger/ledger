package com.revolut.ledger.rest

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.HttpString
import io.undertow.util.Methods
import io.undertow.util.StatusCodes

class CorsHandler(private val next: HttpHandler) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        if (!Methods.OPTIONS.equals(exchange.requestMethod)) {
            throw BadRequestException(
                HandlerResponse(
                    statusCode = StatusCodes.BAD_REQUEST,
                    body = Responses.badMethodError
                )
            )
        } else {
            setCorsResponseHeaders(exchange)
        }
        next.handleRequest(exchange)
    }

    private fun setCorsResponseHeaders(exchange: HttpServerExchange) {
        exchange.responseHeaders
            .add(HttpString("Access-Control-Allow-Origin"), "*")
            .add(HttpString("Access-Control-Allow-Credentials"), "true")
            .add(HttpString("Access-Control-Max-Age"), "1")
            .add(HttpString("Access-Control-Allow-Methods"), "GET, POST")
    }
}
