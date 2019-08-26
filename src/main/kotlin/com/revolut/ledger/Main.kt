package com.revolut.ledger

import com.google.inject.Guice.createInjector
import com.google.inject.Injector
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

fun main() {
    val loadedConfig = loadSystemEnvironmentProperties().withFallback(
        ConfigFactory.load("default-settings.json")!!
    )

    val injector: Injector = createInjector(ApplicationConfiguration(loadedConfig))
    val application = injector.getInstance(Application::class.java)
    Runtime.getRuntime().addShutdownHook(Thread {
        application.stop()
    })
    application.start()
}

private fun loadSystemEnvironmentProperties(): Config =
    ConfigFactory.systemEnvironment().entrySet().map {
        val envVariableKey = it.key
        val envVariableValue = it.value
        val convertedKey = envVariableKey.replace(Regex.fromLiteral("_"), ".").toLowerCase()
        convertedKey to envVariableValue
    }.filter {
        it.first.length > 1
    }.fold(ConfigFactory.empty()) { acc, config ->
        acc.withValue(config.first, config.second)
    }
