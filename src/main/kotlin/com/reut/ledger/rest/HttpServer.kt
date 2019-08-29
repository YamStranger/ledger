package com.reut.ledger.rest

import com.reut.ledger.config.AppConfiguration
import com.reut.ledger.rest.QueryParam.ACCOUNT_ID
import com.reut.ledger.rest.QueryParam.TRANSACTION_ID
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
    private val handlerFactory: HandlerFactory,
    private val appConfiguration: AppConfiguration
) {
    private val logger = KotlinLogging.logger {}
    private var server: Undertow? = null

    fun start() {
        logger.info { "Starting rest service with config: ${appConfiguration.httpServerConfiguration}" }
        val routingHandler = Handlers.routing()
        register(routingHandler, Methods.GET, "/account/{$ACCOUNT_ID}/balance",
            handlerFactory.getAccountBalanceHandler())
        register(routingHandler, Methods.GET, "/account/{$ACCOUNT_ID}/transactions",
            handlerFactory.getAccountTransactionsHandler())
        register(routingHandler, Methods.POST, "/transaction",
            handlerFactory.postTransactionHandler())
        register(routingHandler, Methods.GET, "/transaction/{$TRANSACTION_ID}",
            handlerFactory.getTransactionHandler())
        register(routingHandler, Methods.GET, "/*",
            handlerFactory.getCantFindHandler())
        server = with(appConfiguration.httpServerConfiguration) {
            Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(routingHandler)
                .build()
        }
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
