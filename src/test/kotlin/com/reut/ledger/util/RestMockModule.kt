package com.reut.ledger.util

import com.reut.ledger.rest.handler.AccountBalanceHandler
import com.reut.ledger.rest.handler.AccountTransactionsHandler
import com.reut.ledger.rest.handler.CantFindHandler
import com.reut.ledger.rest.handler.CreateAccountHandler
import com.reut.ledger.rest.handler.CreateTransactionHandler
import com.reut.ledger.rest.handler.TransactionHandler
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.mockk.clearMocks
import io.mockk.mockk

class RestMockModule : KotlinModule() {
    val cantFindHandler: CantFindHandler = mockk()
    val accountBalanceHandler: AccountBalanceHandler = mockk()
    val accountTransactionsHandler: AccountTransactionsHandler = mockk()
    val transactionHandler: TransactionHandler = mockk()
    val createTransactionHandler: CreateTransactionHandler = mockk()
    val createAccountHandler: CreateAccountHandler = mockk()

    override fun configure() {
        bind<CantFindHandler>().toInstance(cantFindHandler)
        bind<AccountBalanceHandler>().toInstance(accountBalanceHandler)
        bind<AccountTransactionsHandler>().toInstance(accountTransactionsHandler)
        bind<TransactionHandler>().toInstance(transactionHandler)
        bind<CreateTransactionHandler>().toInstance(createTransactionHandler)
        bind<CreateAccountHandler>().toInstance(createAccountHandler)
    }

    fun clearMocks() {
        clearMocks(
            cantFindHandler,
            accountBalanceHandler,
            accountTransactionsHandler,
            transactionHandler,
            createTransactionHandler,
            answers = false
        )
    }
}
