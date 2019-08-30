package com.reut.ledger.rest

data class Request<T>(
    val path: String,
    val body: T?,
    val queryParams: Map<QueryParam, String>
)
