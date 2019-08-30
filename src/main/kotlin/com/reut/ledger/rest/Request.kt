package com.reut.ledger.rest

import java.util.UUID

data class Request<T>(
    val requestId: UUID,
    val path: String,
    val body: T?,
    val queryParams: Map<QueryParam, String>
)
