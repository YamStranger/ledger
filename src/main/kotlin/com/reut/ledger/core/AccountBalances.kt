package com.reut.ledger.core

import com.reut.ledger.model.Currency
import java.util.concurrent.ConcurrentHashMap

data class AccountBalances(
    val values: ConcurrentHashMap<Currency, Long>
) {
    operator fun get(currency: Currency): Long = values[currency] ?: 0L
    fun apply(currency: Currency, change: Long) {
        values[currency] = get(currency) + change
    }
}
