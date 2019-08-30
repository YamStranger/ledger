package com.reut.ledger.rest.request

import com.reut.ledger.model.QueryParam
import java.util.UUID

data class Request<T>(
    val requestId: UUID,
    val path: String,
    val body: T?,
    val queryParams: Map<QueryParam, String>
)
