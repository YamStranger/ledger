package com.reut.ledger.core

import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock

fun <T> withAccountsLock(
    times: Int = 10,
    retryPause: Duration = Duration.ofMillis(10),
    lockWait: Duration = Duration.ofMillis(10),
    readOnly: Boolean,
    accountKeys: List<AccountKey>,
    operation: () -> T
): T {
    val listAccountKeys = accountKeys.sortedBy { it.accountId }
    if (times <= 0) {
        throw FiledToObtainLock("Can't obtain lock for accounts $listAccountKeys")
    }
    val a = ReentrantReadWriteLock()
    a.writeLock().tryLock(lockWait.toMillis(), TimeUnit.MILLISECONDS)
    a.readLock().tryLock(lockWait.toMillis(), TimeUnit.MILLISECONDS)

    val locks: List<Lock> = if (readOnly) {
        listAccountKeys.map {
            it.lock.readLock()
        }
    } else {
        listAccountKeys.map {
            it.lock.writeLock()
        }
    }
    return try {
        try {
            val locked = locks.fold(true, { previousOperationResult, lock ->
                previousOperationResult && lock.tryLock(lockWait.toMillis(), TimeUnit.MILLISECONDS)
            })
            val operationResult = if (locked) {
                operation()
            } else {
                throw RetryRequired("Can't obtain lock")
            }
            operationResult
        } finally {
            locks.forEach {
                try {
                    it.unlock()
                } catch (error: IllegalMonitorStateException) {
                    // we should track if this lock hold by this thread or not
                    // right now its safe ignore this operation, so this tread can't unlock resource hold by other thread.
                }
            }
        }
    } catch (retryRequired: RetryRequired) {
        withAccountsLock(
            times = times - 1,
            readOnly = readOnly,
            retryPause = retryPause,
            lockWait = lockWait,
            accountKeys = accountKeys,
            operation = operation
        )
    }
}

fun String.toUUIDorNull(): UUID? {
    return try {
        UUID.fromString(this)
    } catch (error: IllegalArgumentException) {
        null
    }
}
