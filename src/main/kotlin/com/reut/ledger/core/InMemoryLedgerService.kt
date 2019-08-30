package com.reut.ledger.core

import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.Currency
import com.reut.ledger.model.Transaction
import java.util.UUID

class InMemoryLedgerService : LedgerService {
    override fun listTransactions(accountId: UUID): List<Transaction>? {
        return listOf(Transaction(
            id = UUID.randomUUID(),
            from = UUID.randomUUID(),
            to = UUID.randomUUID(),
            currency = Currency.GBP,
            amount = 1000
        ))
    }

    override fun processTransaction(from: UUID,
                                    to: UUID,
                                    currency: Currency,
                                    amount: Long
    ): Result {
        return Result(
            transactionId = UUID.randomUUID()
        )
    }

    override fun getAccountBalance(accountId: UUID, currency: Currency): AccountBalance? {
        return AccountBalance(
            accountId = accountId,
            balance = 1000,
            currency = Currency.GBP
        )
    }

    override fun getTransaction(transactionId: UUID): Transaction? {
        return Transaction(
            id = transactionId,
            from = UUID.randomUUID(),
            to = UUID.randomUUID(),
            currency = Currency.GBP,
            amount = 100
        )
    }
}
