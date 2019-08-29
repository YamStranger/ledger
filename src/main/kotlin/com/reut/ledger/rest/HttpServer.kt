package com.reut.ledger.rest

import com.reut.ledger.rest.QueryParams.ACCOUNT_ID
import com.reut.ledger.rest.handler.HandlerFactory
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.RoutingHandler
import io.undertow.util.HttpString
import io.undertow.util.Methods
import javax.inject.Inject
import mu.KotlinLogging

class HttpServer @Inject constructor(
    private val handlerFactory: HandlerFactory
) {
    private val logger = KotlinLogging.logger {}
    private var server: Undertow? = null

    fun start() {
        val routingHandler = Handlers.routing()
        register(routingHandler, Methods.GET, "/ledger/{$ACCOUNT_ID}/balance",
            handlerFactory.getAccountBalanceHandler())
        register(routingHandler, Methods.GET, "/*",
            handlerFactory.getCantFindHandler())
        server = Undertow.builder()
            .addHttpListener(8081, "127.0.0.1")
            .setHandler(routingHandler)
            .build()
        server!!.start()
    }

    private fun register(
        routing: RoutingHandler,
        method: HttpString,
        template: String,
        handler: HttpHandler
    ): String {
        routing.add(method, template, handler)
        return template
    }

    fun stop() {
        if (server == null) {
            return
        }
        try {
            logger.info("Undertow server is stopping")
            server!!.stop()
        } catch (e: RuntimeException) {
            logger.error("Servlet server stops failed", e)
        }
    }
}
