package com.reut.ledger.config

import com.typesafe.config.Config
import dev.misfitlabs.kotlinguice4.KotlinModule

class ConfigurationModule(private val config: Config) : KotlinModule() {
    companion object {
        const val LOCAL_SERVER_NAME = "app.name"
        const val SERVER_HTTP_HOST = "server.http.host"
        const val SERVER_HTTP_PORT = "server.http.port"
    }

    override fun configure() {
        val httpServerConfiguration = HttpServerConfiguration(
            port = config.getInt(SERVER_HTTP_PORT),
            host = config.getString(SERVER_HTTP_HOST)
        )
        bind<AppConfiguration>().toInstance(AppConfiguration(
            appName = config.getString(LOCAL_SERVER_NAME),
            httpServerConfiguration = httpServerConfiguration
        ))
        bind<HttpServerConfiguration>().toInstance(httpServerConfiguration)
    }
}
