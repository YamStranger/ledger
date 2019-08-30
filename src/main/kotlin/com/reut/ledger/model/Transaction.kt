package com.reut.ledger.model

import java.util.UUID

data class Transaction(
    val id: UUID,
    val from: UUID,
    val to: UUID,
    val currency: Currency,
    val amount: Long
)
