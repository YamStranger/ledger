package com.revolut.ledger.rest

import com.revolut.ledger.rest.ObjectMapper.serialize
import com.revolut.ledger.rest.ObjectMapper.toJson
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

data class Request(
    val path: String
)
