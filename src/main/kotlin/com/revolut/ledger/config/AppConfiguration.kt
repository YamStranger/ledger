package com.revolut.ledger.config

data class AppConfiguration(
    val appName: String = "ledger",
    val httpServerConfiguration: HttpServerConfiguration
)

data class HttpServerConfiguration(
    val port: Int = 8081,
    val host: String = "127.0.0.1"
)
