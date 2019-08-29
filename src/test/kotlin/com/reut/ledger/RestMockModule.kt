package com.reut.ledger

import com.reut.ledger.rest.handler.AccountBalanceHandler
import com.reut.ledger.rest.handler.CantFindHandler
import com.reut.ledger.rest.handler.OkHttpHandler
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.mockk.mockk

class RestMockModule : KotlinModule() {
    val cantFindHandler: CantFindHandler = mockk()
    val okHttpHandler: OkHttpHandler = mockk()
    val accountBalanceHandler: AccountBalanceHandler = mockk()

    override fun configure() {
        bind<CantFindHandler>().toInstance(cantFindHandler)
        bind<OkHttpHandler>().toInstance(okHttpHandler)
        bind<AccountBalanceHandler>().toInstance(accountBalanceHandler)
    }
}
