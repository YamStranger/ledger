package com.revolut.ledger.rest.handler

import com.revolut.ledger.rest.JsonSerializer.serialize
import com.revolut.ledger.rest.JsonSerializer.toJson
import com.revolut.ledger.rest.Request
import com.revolut.ledger.rest.response.HandlerResponse
import com.revolut.ledger.rest.response.HttpResponse
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

abstract class LedgerHandler<T> : HttpHandler {
    final override fun handleRequest(exchange: HttpServerExchange) {
        exchange.addDefaultHeaders()
        val request = Request(
            path = exchange.requestPath
        )
        val response = handleRequest(request)
        exchange.statusCode = response.statusCode
        exchange.responseSender.send(
            serialize(toJson(HttpResponse(
                id = response.id,
                body = response.body
            ))))
    }

    abstract fun handleRequest(request: Request): HandlerResponse<T>
}
