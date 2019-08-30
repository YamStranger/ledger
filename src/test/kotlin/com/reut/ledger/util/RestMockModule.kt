package com.reut.ledger.util

import com.reut.ledger.model.CreateTransactionRequest
import com.reut.ledger.rest.handler.AccountBalanceHandler
import com.reut.ledger.rest.handler.AccountTransactionsHandler
import com.reut.ledger.rest.handler.CantFindHandler
import com.reut.ledger.rest.handler.CreateTransactionHandler
import com.reut.ledger.rest.handler.TransactionHandler
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk

class RestMockModule : KotlinModule() {
    val cantFindHandler: CantFindHandler = mockk()
    val accountBalanceHandler: AccountBalanceHandler = mockk()
    val accountTransactionsHandler: AccountTransactionsHandler = mockk()
    val transactionHandler: TransactionHandler = mockk()
    val createTransactionHandler: CreateTransactionHandler = mockk()

    override fun configure() {
        bind<CantFindHandler>().toInstance(cantFindHandler)
        bind<AccountBalanceHandler>().toInstance(accountBalanceHandler)
        bind<AccountTransactionsHandler>().toInstance(accountTransactionsHandler)
        bind<TransactionHandler>().toInstance(transactionHandler)
        bind<CreateTransactionHandler>().toInstance(createTransactionHandler)

        every { cantFindHandler.getBodyClass() } returns Unit.javaClass
        every { accountBalanceHandler.getBodyClass() } returns Unit.javaClass
        every { accountTransactionsHandler.getBodyClass() } returns Unit.javaClass
        every { transactionHandler.getBodyClass() } returns Unit.javaClass
        every { createTransactionHandler.getBodyClass() } returns CreateTransactionRequest::class.java
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
