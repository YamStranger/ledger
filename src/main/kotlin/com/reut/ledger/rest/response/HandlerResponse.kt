package com.reut.ledger.rest.response

import java.util.UUID

data class HandlerResponse<T>(
    val statusCode: Int,
    val requestId: UUID = UUID.randomUUID(),
    val body: T
)
