package com.reut.ledger.rest.response

import java.util.UUID

/**
 * Each response has UUID, can be used for trace request from frontend to backend logs.
 */
data class HttpResponse<T>(
    val id: UUID = UUID.randomUUID(),
    val body: T
)
