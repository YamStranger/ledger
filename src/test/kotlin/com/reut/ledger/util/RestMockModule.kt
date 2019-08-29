package com.reut.ledger.util

import com.reut.ledger.rest.handler.AccountBalanceHandler
import com.reut.ledger.rest.handler.CantFindHandler
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.mockk.clearMocks
import io.mockk.mockk

class RestMockModule : KotlinModule() {
    val cantFindHandler: CantFindHandler = mockk()
    val accountBalanceHandler: AccountBalanceHandler = mockk()

    override fun configure() {
        bind<CantFindHandler>().toInstance(cantFindHandler)
        bind<AccountBalanceHandler>().toInstance(accountBalanceHandler)
    }

    fun clearMocks() {
        clearMocks(
            cantFindHandler,
            accountBalanceHandler
        )
    }
}
