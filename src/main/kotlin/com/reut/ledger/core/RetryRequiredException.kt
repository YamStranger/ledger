package com.reut.ledger.core

data class RetryRequiredException(override val message: String) : Exception(message)
