package com.reut.ledger.core

enum class ErrorReason {
    ACCOUNT_DOES_NO_EXISTS,
    CANT_DEPOSIT_AMOUNT_BELOW_ZERO,
    ACCOUNTS_FOR_TRANSACTION_SHOULD_BE_DIFFERENT,
    NOT_ENOUGH_BALANCE_TO_EXECUTE_TRANSACTION
}
