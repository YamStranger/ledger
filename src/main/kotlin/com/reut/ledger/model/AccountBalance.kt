package com.reut.ledger.model

import java.util.UUID

data class AccountBalance(
    val accountId: UUID,
    val balances: Map<Currency, Long> // balance in minor units
)
