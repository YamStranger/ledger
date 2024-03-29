package com.reut.ledger.util

import com.reut.ledger.model.CreateTransactionRequest
import com.reut.ledger.model.Currency
import java.util.UUID

object TestRequests {
    val transaction = CreateTransactionRequest(
        from = UUID.randomUUID(),
        to = UUID.randomUUID(),
        currency = Currency.GBP,
        amount = 1000
    )
}
