package com.reut.ledger.model

import java.util.UUID
import java.util.concurrent.locks.ReentrantReadWriteLock

data class AccountKey(
    val lock: ReentrantReadWriteLock = ReentrantReadWriteLock(),
    val accountId: UUID
)
