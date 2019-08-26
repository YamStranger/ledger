package com.revolut.ledger

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.typesafe.config.Config

class ApplicationConfiguration(private val config: Config) : AbstractModule() {

    override fun configure() {
        bindConstant().annotatedWith(Names.named(LOCAL_SERVER_NAME)).to(
            config.getString(LOCAL_SERVER_NAME) + ":" + config.getString(LOCAL_SERVER_VERSION)
        )
        bindConstant().annotatedWith(Names.named(NOTIFICATION_SLACK_CHANNEL)).to(
            config.getString(NOTIFICATION_SLACK_CHANNEL)
        )
        bindConstant().annotatedWith(Names.named(NOTIFICATION_SLACK_KEY)).to(
            config.getString(NOTIFICATION_SLACK_KEY)
        )
        bindConstant().annotatedWith(Names.named(SERVER_HTTP_HOST)).to(
            config.getString(SERVER_HTTP_HOST)
        )
        bindConstant().annotatedWith(Names.named(SERVER_HTTP_PORT)).to(
            config.getInt(SERVER_HTTP_PORT)
        )
        bindConstant().annotatedWith(Names.named(SERVER_HTTP_PORT)).to(
            config.getInt(SERVER_HTTP_PORT)
        )
    }

    companion object {
        const val LOCAL_SERVER_NAME = "app.name"
        const val LOCAL_SERVER_VERSION = "app.version"
        const val NOTIFICATION_SLACK_KEY = "notification.slack.key"
        const val NOTIFICATION_SLACK_CHANNEL = "notification.slack.channel"
        const val SERVER_HTTP_HOST = "server.http.host"
        const val SERVER_HTTP_PORT = "server.http.port"
    }
}
