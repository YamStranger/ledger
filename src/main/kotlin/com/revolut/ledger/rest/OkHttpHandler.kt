package com.revolut.ledger.rest

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

class OkHttpHandler : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.addDefaultHeaders()
        exchange.statusCode = 200
        exchange.responseSender.close()
    }
}
