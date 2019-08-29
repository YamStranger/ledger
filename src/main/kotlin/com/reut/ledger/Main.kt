package com.reut.ledger

import com.google.inject.AbstractModule
import com.google.inject.Guice.createInjector
import com.google.inject.Injector
import com.google.inject.util.Modules
import com.reut.ledger.config.ConfigurationModule
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import dev.misfitlabs.kotlinguice4.getInstance

fun main() {
    startApp(createInjector(loadConfig()))
}

internal fun loadConfig(): Config {
    return loadSystemEnvironmentProperties().withFallback(
        ConfigFactory.load("default-settings.json")!!
    )
}

internal fun createInjector(config: Config, moduleOverrides: List<AbstractModule> = emptyList()) =
    createInjector(Modules.override(ConfigurationModule(config)).with(moduleOverrides))

internal fun startApp(injector: Injector): Application {
    val application = injector.getInstance<Application>()
    Runtime.getRuntime().addShutdownHook(Thread {
        application.stop()
    })
    application.start()
    return application
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
