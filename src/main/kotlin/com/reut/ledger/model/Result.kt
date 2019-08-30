package com.reut.ledger.model

import java.util.UUID

data class Result(
    val transactionId: UUID?,
    val errorReason: ErrorReason? = null
)
