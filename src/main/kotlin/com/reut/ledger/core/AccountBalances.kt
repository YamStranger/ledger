package com.reut.ledger.core

import com.reut.ledger.model.Currency
import java.util.concurrent.ConcurrentHashMap

data class AccountBalances(
    val values: ConcurrentHashMap<Currency, Long>
) {
    operator fun get(currency: Currency): Long = values[currency] ?: 0L
    fun apply(currency: Currency, change: Long) {
        val currentBalance = get(currency)
        if (change > 0 && Long.MAX_VALUE - currentBalance <= change) {
            throw IllegalStateException("Can't apply change $change, because current balance is $currentBalance, and it can be overflow")
        } else if (change < 0 && Long.MIN_VALUE - currentBalance >= change) {
            throw IllegalStateException("Can't apply change $change, because current balance is $currentBalance, and it can be overflow")
        }
        values[currency] = currentBalance + change
    }
}
