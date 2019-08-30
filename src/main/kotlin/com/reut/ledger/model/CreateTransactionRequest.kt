package com.reut.ledger.model

import java.util.UUID

data class CreateTransactionRequest(
    val from: UUID,
    val to: UUID,
    val fromCurrency: Currency,
    val toCurrency: Currency,
    val amount: Long
)
