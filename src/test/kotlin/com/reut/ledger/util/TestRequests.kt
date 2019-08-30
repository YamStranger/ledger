package com.reut.ledger.util

import com.reut.ledger.model.Currency
import com.reut.ledger.model.Transaction
import java.util.UUID

object TestRequests {
    val transaction = Transaction(
        from = UUID.randomUUID(),
        to = UUID.randomUUID(),
        fromCurrency = Currency.GBP,
        toCurrency = Currency.GBP,
        amount = 1000
    )
}
