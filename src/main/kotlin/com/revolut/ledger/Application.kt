package com.revolut.ledger

import com.google.inject.Inject
import com.revolut.ledger.rest.HttpServer

class Application @Inject constructor(
    private val httpServer: HttpServer
) {
    fun start() {
        httpServer.start()
    }

    fun stop() {
        httpServer.stop()
    }
}
