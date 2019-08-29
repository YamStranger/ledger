package com.reut.ledger

import com.google.inject.Inject
import com.reut.ledger.rest.HttpServer

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
