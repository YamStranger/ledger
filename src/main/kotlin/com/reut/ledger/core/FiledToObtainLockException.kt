package com.reut.ledger.core

data class FiledToObtainLockException(override val message: String) : Exception(message)
