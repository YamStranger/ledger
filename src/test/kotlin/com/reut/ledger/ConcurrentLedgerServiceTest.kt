package com.reut.ledger

import com.reut.ledger.core.InMemoryLedgerService
import com.reut.ledger.core.LedgerService.Companion.incomeAccountId
import com.reut.ledger.model.CreateTransactionRequest
import com.reut.ledger.model.Currency
import java.lang.IllegalStateException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class ConcurrentLedgerServiceTest {
    private var ledgerService = InMemoryLedgerService()
    private val threads = Runtime.getRuntime().availableProcessors()
    private val rounds = 10
    private val accountsCount = 100
    private val initialDeposit = 10000L
    private val executor = Executors.newFixedThreadPool(threads)

    @BeforeEach
    fun clearMocks() {
        ledgerService = InMemoryLedgerService()
    }

    @AfterAll
    fun stop() {
        executor.shutdownNow()
    }

    @RepeatedTest(2)
    fun `calculates balance correctly when used with several threads`() {
        val accounts = 0.rangeTo(accountsCount).map { ledgerService.createAccount() }.toList()

        accounts.forEach { account ->
            ledgerService.processTransaction(
                from = incomeAccountId,
                to = account,
                currency = Currency.GBP,
                amount = initialDeposit
            )
        }

        val transactions = mutableListOf<CreateTransactionRequest>()

        // all send 10 coins to all. At the end all users will have same balance as before.
        1.rangeTo(rounds).forEach {
            accounts.forEach { sender ->
                accounts.forEach { receiver ->
                    if (receiver != sender) {
                        transactions += CreateTransactionRequest(
                            from = sender,
                            to = receiver,
                            currency = Currency.GBP,
                            amount = 1
                        )
                    }
                }
            }
        }

        val errors = AtomicInteger(0)
        val runs = AtomicInteger(0)
        val finish = CountDownLatch(transactions.size)
        val start = Semaphore(Int.MAX_VALUE)
        start.acquire(Int.MAX_VALUE)
        val random = ThreadLocalRandom.current()
        do {
            val index = random.nextInt(transactions.size)
            val transaction = transactions.removeAt(index)
            executor.submit {
                start.acquire()
                try {
                    ledgerService.processTransaction(
                        from = transaction.from,
                        to = transaction.to,
                        currency = transaction.currency,
                        amount = transaction.amount
                    ).also {
                        if (it.errorReason != null) {
                            errors.incrementAndGet()
                        }
                    }
                } catch (error: Exception) {
                    errors.incrementAndGet()
                } finally {
                    finish.countDown()
                    runs.incrementAndGet()
                }
            }
        } while (transactions.isNotEmpty())
        start.release(Int.MAX_VALUE)
        finish.await()
        assertEquals(0, errors.get())
        assertEquals(accountsCount * 2 * rounds + 1, ledgerService.listTransactions(accounts.first())!!.size)
        accounts.forEach { account ->
            val balance = ledgerService.getAccountBalance(account)
                ?: throw IllegalStateException("Can't find balance")
            assertEquals(initialDeposit, balance.balances[Currency.GBP])
        }
    }
}
