package com.reut.ledger.core

import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.Currency
import com.reut.ledger.model.Transaction
import java.util.UUID

interface LedgerService {
    /**
     * List all transactions for this account.
     * @return null if there is no such account
     * @param accountId is accountId
     */
    fun listTransactions(accountId: UUID): List<Transaction>?

    /**
     * Process create transaction request.
     * @return Result with transactionId != null and errorReason == null if its success, and this is transaction Id.
     * @return Result with transactionId == null and errorReason != null if its error, and this is transaction Id.
     */
    fun processTransaction(from: UUID,
                           to: UUID,
                           currency: Currency,
                           amount: Long
    ): Result

    /**
     * Returns account balance
     * @return null if there is no such account
     * @param accountId is accountId
     * @param currency currency that used to return balance
     */
    fun getAccountBalance(accountId: UUID, currency: Currency): AccountBalance?

    fun getTransaction(transactionId: UUID): Transaction?
}
