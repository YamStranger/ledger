package com.reut.ledger.rest.handler

import com.reut.ledger.rest.JsonUtil
import com.reut.ledger.rest.JsonUtil.serialize
import com.reut.ledger.rest.QueryParam
import com.reut.ledger.rest.Request
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.HttpResponse
import com.reut.ledger.rest.response.ResponsesFactory
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import java.nio.charset.Charset
import java.util.Deque

class LedgerHttpHandler<M, T>(private val coreHandler: LedgerHandler<M, T>) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.addDefaultHeaders()
        val receiver = exchange.requestReceiver
        receiver.setMaxBufferSize(1024 * 100)
        receiver.receiveFullString({ exch, message ->
            val response = coreHandler.handleRequest(Request(
                path = exchange.requestPath,
                body = message?.let {
                    // FIXME we should find better way to deserialize body for handler
                    val bodyClass = coreHandler.getBodyClass()
                    if (bodyClass != Unit.javaClass) {
                        JsonUtil.deserialize(message, bodyClass)
                    } else {
                        null
                    }
                },
                queryParams = extractQueryParams(exchange.queryParameters)
            ))
            exch.statusCode = response.statusCode
            exch.responseSender.send(
                serialize(HttpResponse(
                    requestId = response.requestId,
                    body = response.body
                )))
        }, Charset.forName("UTF-8"))
    }

    private fun extractQueryParams(pathParams: Map<String, Deque<String>>): Map<QueryParam, String> {
        return try {
            pathParams.mapNotNull {
                if (it.value.isNotEmpty() && it.key != "*") {
                    QueryParam.valueOf(it.key) to it.value.first
                } else {
                    null
                }
            }.toMap()
        } catch (error: IllegalArgumentException) {
            throw BadRequestException(ResponsesFactory.getBadPathParameterError())
        }
    }
}
