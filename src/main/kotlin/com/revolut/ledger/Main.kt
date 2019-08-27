package com.revolut.ledger

import com.google.inject.Guice.createInjector
import com.google.inject.Injector
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import dev.misfitlabs.kotlinguice4.getInstance

fun main() {
    val loadedConfig = loadSystemEnvironmentProperties().withFallback(
        ConfigFactory.load("default-settings.json")!!
    )
    startApp(loadedConfig)
}

internal fun startApp(config: Config): Injector {
    val injector: Injector = createInjector(ApplicationConfiguration(config))
    val application = injector.getInstance<Application>()
    Runtime.getRuntime().addShutdownHook(Thread {
        application.stop()
    })
    application.start()
    return injector
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
