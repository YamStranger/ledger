package com.reut.ledger.core

data class RetryRequired(override val message: String) : Exception(message)
