package com.reut.ledger.core

import java.util.UUID
import java.util.concurrent.locks.ReentrantReadWriteLock

data class AccountKey(
    val lock: ReentrantReadWriteLock = ReentrantReadWriteLock(),
    val accountId: UUID
)
