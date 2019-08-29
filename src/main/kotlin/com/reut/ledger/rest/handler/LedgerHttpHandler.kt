package com.reut.ledger.rest.handler

import com.reut.ledger.rest.JsonUtil.serialize
import com.reut.ledger.rest.JsonUtil.toJson
import com.reut.ledger.rest.QueryParams
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.HttpResponse
import com.reut.ledger.rest.response.ResponsesFactory
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import java.util.Deque

class LedgerHttpHandler<T>(private val coreHandler: LedgerHandler<T>) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.addDefaultHeaders()
        val request = Request(
            path = exchange.requestPath,
            queryParams = extractQueryParams(exchange.queryParameters)
        )
        val response = coreHandler.handleRequest(request)
        exchange.statusCode = response.statusCode
        exchange.responseSender.send(
            serialize(toJson(HttpResponse(
                id = response.id,
                body = response.body
            ))))
    }

    private fun extractQueryParams(pathParams: Map<String, Deque<String>>): Map<QueryParams, String> {
        return try {
            pathParams.mapNotNull {
                if (it.value.isNotEmpty() && it.key != "*") {
                    QueryParams.valueOf(it.key) to it.value.first
                } else {
                    null
                }
            }.toMap()
        } catch (error: IllegalArgumentException) {
            throw BadRequestException(ResponsesFactory.getBadPathParameterError())
        }
    }
}
