package com.reut.ledger.core

import java.util.UUID

data class Result(
    val transactionId: UUID?,
    val errorReason: ErrorReason? = null
)
