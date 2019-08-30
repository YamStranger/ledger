package com.reut.ledger.core

import com.reut.ledger.model.AccountBalance
import com.reut.ledger.model.Currency
import com.reut.ledger.model.Transaction
import mu.KotlinLogging
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * In memory implementation.
 */
class InMemoryLedgerService : LedgerService {
    private val logger = KotlinLogging.logger {}
    private val genesisAccountId = UUID.fromString("00000000-0000-0000-0000-000000000001")

    // If account does not stored in this collection,
    // then it means that this is unknown account
    private val accountKeys = ConcurrentHashMap<UUID, AccountKey>()

    private val balancesCache = ConcurrentHashMap<AccountKey, AccountBalances>()
    private val accountTransactionsCache = ConcurrentHashMap<AccountKey, MutableList<Transaction>>()
    private val transactionsCache = ConcurrentHashMap<UUID, Transaction>()

    init {
        val genesisAccountKey = getAccountKey(
            accountId = genesisAccountId,
            shouldInitIfNotExists = true
        )!!.also {
            logger.info { "Initialized $it" }
        }
        getAccountTransactions(
            accountKey = genesisAccountKey
        ).also {
            logger.info { "Initialized transactions for $genesisAccountKey : $it" }
        }
        val genesisAccountBalances = Currency.values().map {
            it to Int.MAX_VALUE.toLong() // a lot of default money, but not max for type
        }.toMap().toAccountBalances()
        balancesCache += genesisAccountKey to genesisAccountBalances
    }

    override fun listTransactions(accountId: UUID): List<Transaction>? {
        val accountKey = getAccountKey(accountId = accountId, shouldInitIfNotExists = false)
            ?: return null
        return accountTransactionsCache[accountKey]
            ?: throw IllegalStateException("We should have list of transactions for all known accounts")
    }

    override fun processTransaction(from: UUID,
                                    to: UUID,
                                    currency: Currency,
                                    amount: Long
    ): Result {
        if (amount <= 0) {
            return Result(
                transactionId = null,
                errorReason = ErrorReason.CANT_DEPOSIT_AMOUNT_BELOW_ZERO
            )
        }

        val canInitNewAccount = from == genesisAccountId
        val fromAccountKey = getAccountKey(
            accountId = from,
            shouldInitIfNotExists = false
        )
        val toAccountKey = getAccountKey(
            accountId = to,
            shouldInitIfNotExists = canInitNewAccount
        )

        if (toAccountKey == null || fromAccountKey == null) {
            return Result(
                transactionId = null,
                errorReason = ErrorReason.ACCOUNT_DOES_NO_EXISTS
            )
        }

        return withAccountsLock(
            readOnly = false,
            accountKeys = listOf(fromAccountKey, toAccountKey)
        ) {
            val transactionId = UUID.randomUUID()
            val transaction = Transaction(
                id = transactionId,
                from = from,
                to = to,
                currency = currency,
                amount = amount
            )
            val previousDestinationBalance = getAccountBalances(toAccountKey)

            // can be overflow
            if (Long.MAX_VALUE - previousDestinationBalance[currency] < amount) {
                return@withAccountsLock Result(
                    transactionId = null,
                    errorReason = ErrorReason.FINAL_BALANCE_IS_TO_BIG
                )
            }

            val previousSourceBalance = getAccountBalances(fromAccountKey)
            // check if coin balance available for this currency
            if (previousSourceBalance[currency] <= amount) {
                return@withAccountsLock Result(
                    transactionId = null,
                    errorReason = ErrorReason.NOT_ENOUGH_BALANCE_TO_EXECUTE_TRANSACTION
                )
            }
            previousDestinationBalance.apply(currency, amount)
            previousDestinationBalance.apply(currency, -amount)

            transactionsCache += transactionId to transaction
            // here we have guaranty that only this thread works with same accounts
            Result(
                transactionId = transactionId
            )
        }
    }

    /**
     * We should hold lock for accountKey here.
     */
    private fun getAccountBalances(accountKey: AccountKey): AccountBalances {
        return balancesCache.get(accountKey).let {
            if (it == null) {
                val newBalances = Currency.values().toAccountBalances()
                balancesCache.put(accountKey, newBalances)
                newBalances
            } else {
                it
            }
        }
    }

    override fun getAccountBalance(accountId: UUID, currency: Currency): AccountBalance? {
        return AccountBalance(
            accountId = accountId,
            balance = 1000,
            currency = Currency.GBP
        )
    }

    override fun getTransaction(transactionId: UUID): Transaction? {
        return transactionsCache[transactionId]
    }

    private fun Map<Currency, Long>.toAccountBalances(): AccountBalances {
        val concurrentMap = ConcurrentHashMap<Currency, Long>()
        concurrentMap += this
        return AccountBalances(
            concurrentMap
        )
    }

    private fun Array<Currency>.toAccountBalances(): AccountBalances {
        val concurrentMap = ConcurrentHashMap<Currency, Long>()
        for (currency in this) {
            concurrentMap += currency to 0L
        }
        return AccountBalances(
            concurrentMap
        )
    }

    private fun getAccountTransactions(accountKey: AccountKey): List<Transaction> {
        val transactionList = synchronized(accountTransactionsCache) {
            val transactionList = accountTransactionsCache.get(accountKey)
            if (transactionList == null) {
                val newList = CopyOnWriteArrayList<Transaction>()
                accountTransactionsCache.put(accountKey, newList) // this is place could be optimized
            } else {
                transactionList
            }
        }
        return ArrayList(transactionList)
    }

    private fun getAccountKey(accountId: UUID, shouldInitIfNotExists: Boolean): AccountKey? {
        return if (!shouldInitIfNotExists) {
            accountKeys[accountId]
        } else {
            synchronized(accountKeys) {
                val accountKey = accountKeys.get(accountId)
                if (accountKey == null) {
                    val newKey = AccountKey(accountId = accountId)
                    accountKeys.put(accountId, newKey)
                    newKey
                } else {
                    accountKey
                }
            }
        }
    }
}
