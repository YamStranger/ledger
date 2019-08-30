package com.reut.ledger.core

data class FiledToObtainLock(override val message: String) : Exception(message)
