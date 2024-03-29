package com.reut.ledger.rest.handler

import com.reut.ledger.model.QueryParam
import com.reut.ledger.rest.request.Request
import com.reut.ledger.rest.response.BadRequestException
import com.reut.ledger.rest.response.HttpResponse
import com.reut.ledger.rest.response.ResponsesFactory
import com.reut.ledger.rest.util.DeserializationConfigHelper
import com.reut.ledger.rest.util.JsonUtil
import com.reut.ledger.rest.util.JsonUtil.serialize
import com.reut.ledger.rest.util.addDefaultHeaders
import com.reut.ledger.rest.util.requestIdKey
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import java.nio.charset.Charset
import java.util.Deque
import java.util.UUID
import mu.KotlinLogging

class LedgerHttpHandler<M, T>(private val coreHandler: LedgerHandler<M, T>) : HttpHandler {
    private val logger = KotlinLogging.logger {}

    override fun handleRequest(exchange: HttpServerExchange) {
        val requestId = UUID.randomUUID() // TODO config logger to have this request id in messages for this thread
        exchange.putAttachment(requestIdKey, requestId)
        exchange.addDefaultHeaders()
        val receiver = exchange.requestReceiver
        receiver.setMaxBufferSize(1024 * 100)
        receiver.receiveFullString({ exch, message ->
            val request = Request(
                requestId = requestId,
                path = exchange.requestPath,
                body = message?.let {
                    // FIXME we should find better way to deserialize body for handler
                    val bodyClass = DeserializationConfigHelper.getBodyByHandler(coreHandler)
                    if (bodyClass != null) {
                        JsonUtil.deserialize(message, bodyClass)
                    } else {
                        null
                    }
                },
                queryParams = extractQueryParams(exchange.queryParameters)
            )
            logger.info { "Request{$requestId}: $request" }
            val response = coreHandler.handleRequest(request).also {
                logger.info { "Response{$requestId}: $it" }
            }
            exch.statusCode = response.statusCode
            exch.responseSender.send(
                serialize(HttpResponse(
                    requestId = requestId,
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
