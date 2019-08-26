package com.revolut.ledger.rest

import com.google.gson.Gson
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

class CantFindHandler : HttpHandler {
    companion object {
        private val objectMapper = Gson()
    }

    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.addDefaultHeaders()
        exchange.statusCode = 404
        exchange.responseSender.send(
            objectMapper.toJson(Responses.notFoundError)
        )
    }
}
