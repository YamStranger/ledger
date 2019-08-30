## General info
This repo has simple implementation of ledger with rest api.
Ledger supports transactional model with accounts. Account balances not allowed to go below 0, except incomeAccount.
There is no Double used for money operations, all math done in minor units. In future only Long and Scale will be used to represent different currencies.
So, to send 2 GBP you actually need to send 200 cents.

Each account can hold balances in different currencies. Transfers are supported only between balances with same currencies, and there is no exchange operation to convert one currency to other
 currency.

Several endpoints provided: 
- POST, "/account/create": used to create new user account
- GET, "/account/{$ACCOUNT_ID}/balance": used to list account balance in all currencies
- GET, "/account/{$ACCOUNT_ID}/transactions": used to list transactions for this account in all currencies
- POST, "/transaction": used to create new transaction
- GET, "/transaction/{$TRANSACTION_ID}": used to get new transaction info

Used tech:
- Ktlint is used as Code Style tool: [link](https://ktlint.github.io/)
- Swagger is used as API doc tool: [link](https://swagger.io/specification/)
- git-changelog is used to docs for releases: [link](https://www.npmjs.com/package/git-changelog)
- Junit5 is used as testing framework: [link](https://junit.org/junit5/docs/current/user-guide/#overview-what-is-junit-5)
- Mockk is used as mocking framework: [link](https://github.com/mockk/mockk)
- Guice is used as DI framework: [link](https://github.com/google/guice)
- Kotlin is used as language: [link](https://kotlinlang.org/)
- Undertow is used as Rest server: [link](http://undertow.io/)
- Logback is used for Logging
- Fat jars created with Shadow plugin: [link](https://github.com/johnrengelman/shadow)

To avoid dependencies problems gradle is used in mode `resolutionStrategy { failOnVersionConflict() }`.

## Accounts structure
By default only special accounts exists, other accounts use should create:
 - incomeAccount with id `00000000-0000-0000-0001-000000000001`
 - expenseAccount with id `00000000-0000-0000-0001-000000000002`

### incomeAccount
Each user account can get money from incomeAccount, only this account is allowed to go below 0. Nobody can deposit money to incomeAccount, so this account represent all money 
that were send to system. 

Real world example: to represent salary in this ledger we need credit money from  incomeAccount end deposit them to userAccount. Eventually 
incomeAccount will represent for this user all his income

### expenseAccount
Each user account can send money from incomeAccount. Nobody can credit money from expenseAccount, so this account represent all money 
that were send from system. 

Real world example: to represent flat rent in this ledger we need credit money from userAccount end deposit them to expenseAccount. Eventually 
expenseAccount will represent for this user all his spendings.


Transfers between accounts can be done, but only till their balance is > 0.

### Use case
1. Create account for user (for example `00000000-0000-0000-0000-000000000001`)
2. User can list transactions for account `00000000-0000-0000-0000-000000000001`, now its zero transaction.
3. User can get balance for account `00000000-0000-0000-0000-000000000001`, all currencies == 0
4. User can debit balance for account `00000000-0000-0000-0000-000000000001` by creating transaction that will send money from
incomeAccount with id `00000000-0000-0000-0001-000000000001` to `00000000-0000-0000-0000-000000000001`
5. `00000000-0000-0000-0000-000000000001` now contains 1 transaction
6. Balance of `00000000-0000-0000-0000-000000000001` is > 0
7. `00000000-0000-0000-0001-000000000001` now contains 1 transaction
8. Balance of `00000000-0000-0000-0001-000000000001` is < 0
9. Create account for second user (for example `00000000-0000-0000-0000-000000000002`)
10. User can create transaction to send money from `00000000-0000-0000-0000-000000000001` to `00000000-0000-0000-0000-000000000002`
11. User can create transaction to send money from `00000000-0000-0000-0000-000000000001` to expenseAccount with id `00000000-0000-0000-0001-000000000002`

Please do this operations from swagger site/editor, based on `Api Documentation`.

## Build application
Local gradlew is used. So to build server you need only run command from project root
`./gradlew clean shadowJar`

This will build project and prepare runnable in dir `./build/libs/` with name `reut-ledger-1.0.0-all.jar`

P.S. you can simply execute `./build.sh`

## Run application
After application was build just execute command from project root
`java -jar build/libs/reut-ledger-1.0.0-all.jar`
It will run server with default config `HttpServerConfiguration(port=8081, host=127.0.0.1)` 

P.S. you can simply execute `./buildAndRun.sh`

## Api documentation
High details structure of ledger:
 - each response has requestId and actual body with response object
 - each error response has requestId and error object
 - each request internally has requestId, and this included in logs for traceability

Swagger spec created for this service. Its fully interactive, and if you have running service in background you can use
this spec to send rest calls, edit fields, read responses, etc.

How to use Swagger Api Docs without Idea: 
 1. open https://editor.swagger.io/
 2. copy spec/reut-ledger.yaml to editor [file](https://github.com/YamStranger/ledger/blob/master/spec/reut-ledger.yaml)
 3. run server locally (see [Run application] step)
 4. check how server works with swagger
 
 How to use Swagger Api Docs with Idea: 
  1. install plugin [link](https://plugins.jetbrains.com/plugin/8347-swagger/) 
  2. open in idea spec/reut-ledger.yaml
  3. run server locally (see [Run application] step)
  4. plugin will show option in edit window to open swagger spec in browser. To open interactive page - just press on that button.
 
## Developing application
Branch name conversion: JIRA-ISSUE1/{feat,chore,test}/name_of_feature
Commit name conversion: [link](https://gist.github.com/stephenparish/9941e89d80e2bc58a153#format-of-the-commit-message)
Releases change log: [link](https://www.npmjs.com/package/git-changelog)

Releases description example:

```
Tag 1.0.0

Summary: Some important features

Change log: link

Changes generated with https://www.npmjs.com/package/git-changelog
```

Releases: [link](https://github.com/YamStranger/ledger/releases)

