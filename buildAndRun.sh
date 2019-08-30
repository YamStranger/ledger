#!/usr/bin/env bash

./gradlew clean build shadowJar
java -jar build/libs/reut-ledger-0.0.1-all.jar
