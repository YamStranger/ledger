package com.reut.ledger.rest.handler

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.HttpString

class CorsHandler(private val next: HttpHandler) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        setCorsResponseHeaders(exchange)
        next.handleRequest(exchange)
    }

    private fun setCorsResponseHeaders(exchange: HttpServerExchange) {
        exchange.responseHeaders
            .add(HttpString("Access-Control-Allow-Origin"), "*")
            .add(HttpString("Access-Control-Allow-Credentials"), "true")
            .add(HttpString("Access-Control-Allow-Headers"), "*")
            .add(HttpString("Access-Control-Max-Age"), "1")
            .add(HttpString("Access-Control-Allow-Methods"), "GET, POST, OPTIONS")
    }
}
