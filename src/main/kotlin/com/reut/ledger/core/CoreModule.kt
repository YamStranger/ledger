package com.reut.ledger.core

import dev.misfitlabs.kotlinguice4.KotlinModule

class CoreModule : KotlinModule() {
    override fun configure() {
        bind<LedgerService>().toInstance(InMemoryLedgerService())
    }
}
