## General info
This repo has simple implementation of ledger.
Ledger supports transaction model based on accounts. Account balances not allowed to go below 0.
There is no double used for money operations, all math done in minor units.

In future only Long and Scale will be used to represent different
currencies. Currently only one currency is used and all api build around minor units (cents in case of GBP), so to send 
2 GBP you actually need to send 200 cents.

Each account can hold balances in different currencies. Transfers are supported only between same balances.
To convert one currency to other currency user should do exchange operation.

Api supports currency, but only one currency actually allowed right now - GBP.

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

To avoid dependencies problems gradle in mode `resolutionStrategy { failOnVersionConflict() }` is used.

## Balances
By default only one account exists: `00000000-0000-0000-0000-000000000001`(*Genesis*) with infinite balance.

Others account can be created by doing transaction from *Genesis* account to your account.

Transfers between accounts can be done, but only till their balance is > 0.

Usage flow:
1. Create transaction to transfer money from `00000000-0000-0000-0000-000000000001`(*Genesis*) account to user account: for example `00000000-0000-0000-0000-000000000002`. After this operation
 account will be created, and one transaction will be stored for this account. 
2. User can list transactions for account `00000000-0000-0000-0000-000000000002`, now its only one transaction.
3. User can get balance for account `00000000-0000-0000-0000-000000000002`
4. Create transaction to transfer money from `00000000-0000-0000-0000-000000000001`(*Genesis*) account to user account: for example `00000000-0000-0000-0000-000000000003`. . After this operation
account will be created, and one transaction will be stored for this account.
5. User can list transactions for account `00000000-0000-0000-0000-000000000003`, now its only one transaction.
6. User can get balance for account `00000000-0000-0000-0000-000000000003`
7. Create transaction to transfer money from `00000000-0000-0000-0000-000000000002` to `00000000-0000-0000-0000-000000000003`
8. Both balances are changed

Please do this operations from swagger, based on `Api Documentation`.

## Build application
Local gradlew is used. So to build server you need only run command form project rule
`./gradlew clean shadowJar`

This will build project and will prepare runnable in dir `build/libs/` with name `reut-ledger-0.0.1-all.jar`

P.S. you can simply execute `./build.sh`

## Run application
After application was build just execute command from project root
`java -jar build/libs/reut-ledger-0.0.1-all.jar`
It will run server with default config `HttpServerConfiguration(port=8081, host=127.0.0.1)` 

P.S. you can simply execute `./buildAndRun.sh`

## Api documentation
General idea:
 - each response has requestId and actual body with response object
 - each error response has requestId and error object

Api Docs: 
 1 open https://editor.swagger.io/
 2 copy spec/reut-ledger.yaml to editor
 3 run server locally (see [Run application] step)
 4 check how server works with swagger
 
## Developing application
Branch name conversion: JIRA-ISSUE1/{feat,chore,test}/name_of_feature
Commit name conversion: [link](https://gist.github.com/stephenparish/9941e89d80e2bc58a153#format-of-the-commit-message)

Releases description example:

```
Tag 1.0.0

Summary: Some important features

Change log: link

Changes generated with https://www.npmjs.com/package/git-changelog
```

