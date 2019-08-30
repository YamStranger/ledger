package com.reut.ledger

import com.reut.ledger.core.InMemoryLedgerService
import com.reut.ledger.core.LedgerService.Companion.expenseAccountId
import com.reut.ledger.core.LedgerService.Companion.incomeAccountId
import com.reut.ledger.model.Currency
import com.reut.ledger.model.ErrorReason
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class InMemoryLedgerServiceTest {
    private var ledgerService = InMemoryLedgerService()

    @BeforeEach
    fun clearMocks() {
        ledgerService = InMemoryLedgerService()
    }

    @Test
    fun `initializes accounts with zero balance`() {
        val userAccount = ledgerService.createAccount()
        val userBalances = ledgerService.getAccountBalance(userAccount)
            ?: throw IllegalStateException("Can't find balance")
        assertEquals(Currency.values().size, userBalances.balances.size)
        assertEquals(0, userBalances.balances.map { it.value }.sum())
    }

    @Test
    fun `returns null responses if  account as not created`() {
        assertNull(ledgerService.getAccountBalance(UUID.randomUUID()))
        assertNull(ledgerService.listTransactions(UUID.randomUUID()))
    }

    @Test
    fun `initializes accounts with zero transactions`() {
        val userAccount = ledgerService.createAccount()
        val userTransactions = ledgerService.listTransactions(userAccount)
            ?: throw IllegalStateException("Can't find transactions collection")
        assertEquals(0, userTransactions.size)
    }

    @Test
    fun `adds money to account from income account`() {
        val userAccount = ledgerService.createAccount()
        val result = ledgerService.processTransaction(
            from = incomeAccountId,
            to = userAccount,
            currency = Currency.GBP,
            amount = 1000
        )
        assertNull(result.errorReason)

        checkBalances(userAccount, mapOf(
            Currency.GBP to 1000L,
            Currency.EUR to 0L
        ))

        checkBalances(incomeAccountId, mapOf(
            Currency.GBP to -1000L,
            Currency.EUR to 0L
        ))
    }

    @Test
    fun `does not allow create negative balances`() {
        val userAccount = ledgerService.createAccount()
        val otherUserAccount = ledgerService.createAccount()
        val result = ledgerService.processTransaction(
            from = userAccount,
            to = otherUserAccount,
            currency = Currency.GBP,
            amount = 1000
        )
        assertNotNull(result.errorReason)
        assertEquals(ErrorReason.NOT_ENOUGH_BALANCE_TO_EXECUTE_TRANSACTION, result.errorReason)
        checkBalances(userAccount, mapOf(
            Currency.GBP to 0L,
            Currency.EUR to 0L
        ))

        checkBalances(otherUserAccount, mapOf(
            Currency.GBP to 0L,
            Currency.EUR to 0L
        ))
    }

    @Test
    fun `does not allow overflow during simple operation`() {
        val userAccount = ledgerService.createAccount()
        ledgerService.processTransaction(
            from = incomeAccountId,
            to = userAccount,
            currency = Currency.GBP,
            amount = 10
        )

        assertThrows(IllegalStateException::class.java) {
            ledgerService.processTransaction(
                from = incomeAccountId,
                to = userAccount,
                currency = Currency.GBP,
                amount = Long.MAX_VALUE
            )
        }

        checkBalances(userAccount, mapOf(
            Currency.GBP to 10L,
            Currency.EUR to 0L
        ))

        checkBalances(incomeAccountId, mapOf(
            Currency.GBP to -10L,
            Currency.EUR to 0L
        ))
    }

    @Test
    fun `does not allow deposit income account`() {
        val userAccount = ledgerService.createAccount()
        ledgerService.processTransaction(
            from = incomeAccountId,
            to = userAccount,
            currency = Currency.GBP,
            amount = 10
        )

        val result = ledgerService.processTransaction(
            from = userAccount,
            to = incomeAccountId,
            currency = Currency.GBP,
            amount = 10
        )

        assertNotNull(result.errorReason)
        assertEquals(ErrorReason.CANT_DEPOSIT_TO_INCOME_ACCOUNT, result.errorReason)

        checkBalances(userAccount, mapOf(
            Currency.GBP to 10L,
            Currency.EUR to 0L
        ))

        checkBalances(incomeAccountId, mapOf(
            Currency.GBP to -10L,
            Currency.EUR to 0L
        ))
    }

    @Test
    fun `does not allow credit expenses account`() {
        val userAccount = ledgerService.createAccount()

        val result = ledgerService.processTransaction(
            from = expenseAccountId,
            to = userAccount,
            currency = Currency.GBP,
            amount = 10
        )

        assertNotNull(result.errorReason)
        assertEquals(ErrorReason.CANT_CREDIT_EXPENSES_ACCOUNT, result.errorReason)
        checkBalances(userAccount, mapOf(
            Currency.GBP to 0L,
            Currency.EUR to 0L
        ))

        checkBalances(expenseAccountId, mapOf(
            Currency.GBP to 0L,
            Currency.EUR to 0L
        ))
    }

    @Test
    fun `debits expenses account`() {
        val userAccount = ledgerService.createAccount()
        ledgerService.processTransaction(
            from = incomeAccountId,
            to = userAccount,
            currency = Currency.GBP,
            amount = 10
        )

        val result = ledgerService.processTransaction(
            from = userAccount,
            to = expenseAccountId,
            currency = Currency.GBP,
            amount = 10
        )
        assertNull(result.errorReason)

        checkBalances(userAccount, mapOf(
            Currency.GBP to 0L,
            Currency.EUR to 0L
        ))

        checkBalances(expenseAccountId, mapOf(
            Currency.GBP to 10L,
            Currency.EUR to 0L
        ))

        checkBalances(incomeAccountId, mapOf(
            Currency.GBP to -10L,
            Currency.EUR to 0L
        ))
    }

    fun checkBalances(accountId: UUID, balances: Map<Currency, Long>) {
        val accountBalance = ledgerService.getAccountBalance(accountId)
            ?: throw IllegalStateException("Can't find balance")
        balances.forEach { currency, balance ->
            assertEquals(balance, accountBalance.balances[currency])
        }
    }
}
