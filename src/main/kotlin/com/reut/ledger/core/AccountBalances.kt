package com.reut.ledger.core

import com.reut.ledger.model.Currency
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

data class AccountBalances(
    val values: ConcurrentHashMap<Currency, Long>
) {
    operator fun get(currency: Currency): Long = values[currency] ?: 0L
    fun apply(currency: Currency, change: Long) {
        val currentBalance = get(currency)

        when {
            change > 0 && currentBalance > 0 && Long.MAX_VALUE - currentBalance <= change ||
                change < 0 && currentBalance < 0 && abs(Long.MIN_VALUE) - abs(currentBalance) < abs(change) ->
                throw IllegalStateException("Can't apply change $change, because current balance is $currentBalance, and it can be overflow")
        }
        values[currency] = currentBalance + change
    }
}
