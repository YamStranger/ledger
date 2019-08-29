package com.reut.ledger.rest

data class Request(
    val path: String,
    val queryParams: Map<QueryParam, String>
)
