package com.reut.ledger.rest.util

import com.reut.ledger.model.CreateTransactionRequest
import com.reut.ledger.rest.handler.CreateTransactionHandler
import com.reut.ledger.rest.handler.LedgerHandler

object DeserializationConfigHelper {
    private val bodyTypes = mapOf(CreateTransactionHandler::class.java.canonicalName to CreateTransactionRequest::class.java)

    @Suppress("UNCHECKED_CAST")
    fun <M, T> getBodyByHandler(handler: LedgerHandler<M, T>): Class<M>? {
        val type = bodyTypes[handler::class.java.canonicalName]
        return if (type != null) {
            type as Class<M>
        } else {
            null
        }
    }
}
