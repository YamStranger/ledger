# General info

This repo has simple implementation of ledger

# Money operations
There is double used for money operations. In future only Long and Scale will be used to represent different
currencies.
Currently only one currency is used and all api build around minor units (cents in case of GBP), so to send 
2 GBP you actually need to send 200 cents.

# Balances
By default each account does not exists.
Only one account exists: `00000000-0000-0000-0000-000000000001`(*Genesis*) with infinite balance
To create account, you need add transaction from *Genesis* account to your account.

# Api documentation

# Build application

# Run application


