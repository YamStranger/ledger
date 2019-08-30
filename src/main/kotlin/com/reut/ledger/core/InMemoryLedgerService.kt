package com.reut.ledger.core

import com.reut.ledger.core.LedgerService.Companion.expenseAccountId
import com.reut.ledger.core.LedgerService.Companion.incomeAccountId
import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.Currency
import com.reut.ledger.model.Transaction
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * In memory implementation.
 */
class InMemoryLedgerService : LedgerService {
    // If account does not stored in this collection,
    // then it means that this is unknown account
    private val accountKeys = ConcurrentHashMap<UUID, AccountKey>()

    private val balancesCache = ConcurrentHashMap<AccountKey, AccountBalances>()

    // Some redundancy here, because we could store UUID of transaction, and lookup it in transactionsCache, but its overhead
    private val accountTransactionsCache = ConcurrentHashMap<AccountKey, MutableList<Transaction>>()
    private val transactionsCache = ConcurrentHashMap<UUID, Transaction>()

    init {
        createAccount(incomeAccountId)
        createAccount(expenseAccountId)
    }

    override fun createAccount(): UUID {
        val newAccountId = UUID.randomUUID()
        createAccount(newAccountId)
        return newAccountId
    }

    override fun listTransactions(accountId: UUID): List<Transaction>? {
        val accountKey = getAccountKey(accountId = accountId)
            ?: return null
        return withAccountsLock(
            readOnly = true,
            accountKeys = listOf(accountKey)
        ) {
            val transactions = accountTransactionsCache[accountKey]
                ?: throw IllegalStateException("We should have list of transactions for all known accounts")
            ArrayList(transactions)
        }
    }

    override fun processTransaction(
        from: UUID,
        to: UUID,
        currency: Currency,
        amount: Long
    ): Result {
        val sourceAccountKey = getAccountKey(accountId = from)
        val destinationAccountKey = getAccountKey(accountId = to)

        if (amount <= 0) {
            return Result(
                transactionId = null,
                errorReason = ErrorReason.CANT_DEPOSIT_AMOUNT_BELOW_ZERO
            )
        } else if (destinationAccountKey == null || sourceAccountKey == null) {
            return Result(
                transactionId = null,
                errorReason = ErrorReason.ACCOUNT_DOES_NO_EXISTS
            )
        }

        return withAccountsLock(
            readOnly = false,
            accountKeys = listOf(sourceAccountKey, destinationAccountKey)
        ) {
            val transactionId = UUID.randomUUID()
            val transaction = Transaction(
                id = transactionId,
                from = from,
                to = to,
                currency = currency,
                amount = amount,
                createdAt = Instant.now().toEpochMilli()
            )
            val previousDestinationBalance = getAccountBalances(destinationAccountKey)
            val previousSourceBalance = getAccountBalances(sourceAccountKey)

            // check if coin balance available for this currency
            if (sourceAccountKey.accountId != incomeAccountId && previousSourceBalance[currency] <= amount) {
                return@withAccountsLock Result(
                    transactionId = null,
                    errorReason = ErrorReason.NOT_ENOUGH_BALANCE_TO_EXECUTE_TRANSACTION
                )
            }
            previousDestinationBalance.apply(currency, amount)
            previousSourceBalance.apply(currency, -amount)

            transactionsCache += transactionId to transaction
            recordTransaction(transaction, destinationAccountKey, sourceAccountKey)
            Result(
                transactionId = transactionId
            )
        }
    }

    override fun getAccountBalance(accountId: UUID, currency: Currency): AccountBalance? {
        val accountKey = getAccountKey(accountId = accountId)
            ?: return null
        return withAccountsLock(
            readOnly = true,
            accountKeys = listOf(accountKey)
        ) {
            val accountBalances = getAccountBalances(accountKey)
            AccountBalance(
                accountId = accountId,
                balance = accountBalances[currency],
                currency = currency
            )
        }
    }

    override fun getTransaction(transactionId: UUID): Transaction? {
        return transactionsCache[transactionId]
    }

    private fun createAccount(newAccountId: UUID) {
        val accountKey = AccountKey(accountId = newAccountId)

        withAccountsLock(
            readOnly = false,
            accountKeys = listOf(accountKey)
        ) {
            if (accountKeys.putIfAbsent(newAccountId, accountKey) != null) {
                throw IllegalStateException("Wow, we generated same UUID twice. Can't create account")
            }
            initAccountTransactionsStorage(accountKey)
            initAccountBalancesStorage(accountKey)
        }
    }

    private fun initAccountTransactionsStorage(accountKey: AccountKey) {
        val newList = CopyOnWriteArrayList<Transaction>()
        accountTransactionsCache[accountKey] = newList
    }

    private fun initAccountBalancesStorage(accountKey: AccountKey) {
        val genesisAccountBalances = Currency.values().toAccountBalances()
        balancesCache += accountKey to genesisAccountBalances
    }

    /**
     * We should hold lock for accountKey here.
     */
    private fun recordTransaction(transaction: Transaction, vararg accountKeys: AccountKey) {
        accountKeys.forEach { key ->
            val transactions = accountTransactionsCache[key]
                ?: throw IllegalStateException("We should have transaction list initialized for each account")
            transactions.add(transaction)
        }
    }

    /**
     * We should hold lock for accountKey here.
     */
    private fun getAccountBalances(accountKey: AccountKey) = balancesCache[accountKey]
        ?: throw IllegalStateException("Balances should initialized for all users")

    private fun Array<Currency>.toAccountBalances(): AccountBalances {
        val concurrentMap = ConcurrentHashMap<Currency, Long>()
        for (currency in this) {
            concurrentMap += currency to 0L
        }
        return AccountBalances(
            concurrentMap
        )
    }

    private fun getAccountKey(accountId: UUID): AccountKey? = accountKeys[accountId]
}
