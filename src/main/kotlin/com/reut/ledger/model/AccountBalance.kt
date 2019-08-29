package com.reut.ledger.model

import java.util.UUID

data class AccountBalance(
    val accountId: UUID,
    val balance: Long, // balance in minor units
    val currency: Currency
)
