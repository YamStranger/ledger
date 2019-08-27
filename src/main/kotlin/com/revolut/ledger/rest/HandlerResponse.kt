package com.revolut.ledger.rest

import java.util.UUID

data class HandlerResponse<T>(
    val statusCode: Int,
    val id: UUID = UUID.randomUUID(),
    val body: T
)
